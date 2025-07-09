package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestCreateRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestDeleteRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestGetResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestPatchRequest;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeHistory;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeRequest;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeUser;
import com.venvas.pocamarket.service.trade.domain.enums.TcgTradeRequestStatus;
import com.venvas.pocamarket.service.trade.domain.enums.TradeStatus;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeException;
import com.venvas.pocamarket.service.trade.domain.repository.*;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TcgTradeRequestService {
    private final TcgCodeRepository tcgCodeRepository;
    private final TcgTradeRequestRepository tcgTradeRequestRepository;
    private final TcgTradeHistoryRepository tcgTradeHistoryRepository;
    private final TcgTradeRepository tcgTradeRepository;
    private final TcgTradeUserRepository tcgTradeUserRepository;
    private final UserRepository userRepository;
    private final TcgTradeUserService tcgTradeUserService;

    private final Integer STATUS_ACTIVE = 1;


    /**
     * 교환 요청 생성
     */
    @Transactional
    public Boolean createTcgTradeRequest(Long tradeId, TcgTradeRequestCreateRequest request, String userUuid) {
        // 기본 검증
        validateUserUuid(userUuid);

        // 친구 코드 존재 여부 확인
        boolean tcgCodeExists = tcgCodeRepository.existsByUuidAndTcgCodeAndStatus(userUuid, request.getTcgCode(), STATUS_ACTIVE);
        if (!tcgCodeExists) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_TCG_CODE_FORMAT, "등록되지 않은 친구 코드입니다.");
        }

        // 필요한 정보 조회
        TcgTrade trade = findTrade(tradeId);

        if (trade.getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS, "본인이 작성한 교환 글에는 요청할 수 없습니다.");
        }

        if(trade.getStatus() > TradeStatus.SELECT.getCode()) {
            TcgTradeErrorCode errorCode = TcgTradeErrorCode.TRADE_ALREADY_PROCESS;

            if(trade.getStatus().equals(TradeStatus.COMPLETE.getCode())) {
                errorCode = TcgTradeErrorCode.TRADE_ALREADY_COMPLETED;
            }

            throw new TcgTradeException(errorCode);
        }

        // 중복 카드 검사
        if(tcgTradeRequestRepository.existsByTradeIdAndUuidAndRequestCardCode(trade.getId(), userUuid, request.getCardCode())) {
            throw new TcgTradeException(TcgTradeErrorCode.DUPLICATE_TRADE_REQUEST);
        }

        User user = findUser(userUuid);

        // 교환 글에 해당하는 교환 요청글이 있는지 확인
        boolean tradeHasRequest = tcgTradeRequestRepository.existsByTradeId(trade.getId());

        // 교환 요청 생성 및 저장
        TcgTradeRequest savedTradeRequest = tcgTradeRequestRepository.save(new TcgTradeRequest(
                trade, userUuid, user.getNickname(), request.getTcgCode(),
                request.getCardCode(), TcgTradeRequestStatus.REQUEST.getCode()
        ));

        // 히스토리 저장
        String historyContent = String.format("%s님이 %s (%s)카드로 교환을 요청했습니다.",
                user.getNickname(), request.getCardName(), request.getCardCode());
        saveHistory(trade, savedTradeRequest, userUuid, historyContent);

        // 교환 글에 요청이 없었으면 (삭제된 요청 제외, 요청글 추가되기 전에 검사) 상태 변경
        if(!tradeHasRequest) {
            trade.updateStatus(TradeStatus.SELECT.getCode());
            saveHistory(trade, savedTradeRequest, trade.getUuid(), "교환글의 상태가 (교환 선택)으로 변경 되었습니다.");
        }

        return true;
    }

    /**
     * 교환 요청리스트 가져오기
     */
    public List<TcgTradeRequestGetResponse> getTcgTradeRequestList(Long tradeId, String userUuid, Boolean isAdmin) {
        validateTradeExists(tradeId);
        return tcgTradeRequestRepository.findTradeRequestsWithTradeUser(tradeId, userUuid, isAdmin);
    }

    /**
     * 교환 요청 수정
     */
    @Transactional
    public Boolean patchTcgTradeRequest(Long tradeId, TcgTradeRequestPatchRequest request, String userUuid) {
        
        // 기본 검증
        validateUserUuid(userUuid);
        validateRequestNextStatus(request.getStatus());

        // 교환 요청 조회 및 권한 검증
        TcgTradeRequest tcgTradeRequest = findTradeRequest(request.getTcgTradeRequestId(), tradeId); // 삭제 된게 아닌 것들 중에서 찾음
        TcgTrade trade = tcgTradeRequest.getTrade();

        validateIsTradeOwner(tcgTradeRequest, userUuid);
        validateCurrentStatus(tcgTradeRequest, request.getStatus());

        if(trade.getStatus().equals(TradeStatus.SELECT.getCode()) && tcgTradeRequest.getStatus() > TcgTradeRequestStatus.REQUEST.getCode()) {
            // 교환글의 상태가 선택중, 요청의 상태가 Request(1) 보다 크면 에러
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_NOT_EDITABLE);
        } else if(trade.getStatus().equals(TradeStatus.PROCESS.getCode()) && tcgTradeRequest.getStatus() < TcgTradeRequestStatus.PROCESS.getCode()) {
            // 교환글의 상태가 진행중 or 교환 완료인데 요청글이 진행중보다 낮으면 에러
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_ALREADY_IN_PROGRESS);
        } else if(trade.getStatus().equals(TradeStatus.COMPLETE.getCode())) {
            // 교환글의 상태가 완료인데 요청 들어오면 에러
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_ALREADY_IN_PROGRESS);
        } else if(trade.getStatus().equals(TradeStatus.DELETED.getCode())) {
            // 교환글의 상태가 삭제인데 요청 들어오면 에러
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_ALREADY_CANCELLED);
        }

        // 다음 단계로 상태 업데이트
        Integer nextStatus = getNextRequestStatusSafely(tcgTradeRequest.getStatus());
        tcgTradeRequest.updateStatus(nextStatus);
        TcgTradeRequest savedTradeRequest = tcgTradeRequestRepository.save(tcgTradeRequest);

        // 게시글 상태도 업데이트
        Integer nextTradeStatus = getNextTradeStatusSafely(trade.getStatus());
        trade.updateStatus(nextTradeStatus);
        tcgTradeRepository.save(trade);

        // 히스토리 저장
        // 교환글
        String tradeHistoryMessage = trade.getStatus().equals(TradeStatus.PROCESS.getCode()) ?
                "교환글의 상태가 (교환 진행)으로 변경 되었습니다." : "교환글의 상태가 (교환 완료)으로 변경 되었습니다.";
        saveHistory(trade, savedTradeRequest, trade.getUuid(), tradeHistoryMessage);

        // 교환 요청
        String historyContent = createStatusChangeHistoryContent(nextStatus, tcgTradeRequest.getNickname());
        saveHistory(tcgTradeRequest.getTrade(), savedTradeRequest, userUuid, historyContent);

        // 교환 완료 시 거래 횟수 증가 및 보상 지급
        if (nextStatus.equals(TcgTradeRequestStatus.COMPLETE.getCode())) {
            updateTradeCount(tcgTradeRequest.getUuid(), tcgTradeRequest.getTrade().getUuid());
            // 완료처리시 포인트 지급, 경험치 지급
            tcgTradeUserService.giveRewardToUser(tcgTradeRequest.getUuid()); 
            tcgTradeUserService.giveRewardToUser(tcgTradeRequest.getTrade().getUuid());
        }

        return true;
    }

    /**
     * 교환 요청 삭제
     */
    @Transactional
    public Boolean deleteTcgTradeRequest(Long tradeId, TcgTradeRequestDeleteRequest request, String userUuid, Boolean isAdmin) {

        // 기본 검증
        validateUserUuid(userUuid);

        // 교환 요청 조회 및 권한 검증
        TcgTradeRequest tcgTradeRequest = findTradeRequest(request.getTcgTradeRequestId(), tradeId);
        validateDeletePermission(tcgTradeRequest, userUuid, isAdmin);
        validateDeletableStatus(tcgTradeRequest);

        // 교환글 가져오기
        TcgTrade trade = findTrade(tradeId);

        // 삭제 처리
        tcgTradeRequest.updateStatus(TcgTradeRequestStatus.DELETE.getCode());
        TcgTradeRequest savedTradeRequest = tcgTradeRequestRepository.save(tcgTradeRequest);

        // 삭제 이후, 교환글에 가장 상태가 큰 요청을 기준으로 교환글 상태 변경
        Optional<TcgTradeRequest> topStatusRequest = tcgTradeRequestRepository.findFirstByTradeIdOrderByStatusDesc(trade.getId());

        int topStatus = topStatusRequest.isPresent() ? topStatusRequest.get().getStatus() : 0;
        Integer tradeStatus = switch (topStatus) {
            case 0 -> TradeStatus.REQUEST.getCode();    // 삭제 요청 밖에 없음
            case 1 -> TradeStatus.SELECT.getCode();     // 요청글이 있음
            case 2 -> TradeStatus.PROCESS.getCode();    // 교환 진행중인 요청 있음
            case 3 -> TradeStatus.COMPLETE.getCode();   // 교환 완료
            default -> throw new TcgTradeException(TcgTradeErrorCode.INVALID_TRADE_STATUS);
        };

        // 히스토리 저장
        // 요청
        String historyContent = createDeleteHistoryContent(tcgTradeRequest.getNickname(), isAdmin);
        saveHistory(tcgTradeRequest.getTrade(), savedTradeRequest, userUuid, historyContent);
        // 교환글
        trade.updateStatus(tradeStatus);
        String message = String.format("교환글의 상태가 %s으로 변경 되었습니다.", TradeStatus.fromDbCode(tradeStatus).getDescription());
        saveHistory(tcgTradeRequest.getTrade(), savedTradeRequest, trade.getUuid(), message);

        return true;
    }

    /* 사용자 UUID 유효성을 검증합니다. */
    private void validateUserUuid(String userUuid) {
        if (userUuid == null || userUuid.isBlank()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    /* 교환 글 존재 여부를 확인합니다. */
    private void validateTradeExists(Long tradeId) {
        if (!tcgTradeRepository.existsById(tradeId)) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND);
        }
    }

    /* 교환 요청을 조회합니다. */
    private TcgTradeRequest findTradeRequest(Long tcgTradeRequestId, Long tradeId) {
        return tcgTradeRequestRepository
                .findByIdAndTradeId(tcgTradeRequestId, tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_NOT_FOUND));
    }

    /* 사용자 정보를 조회합니다. */
    private User findUser(String userUuid) {
        return userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    /* 교환 글을 조회합니다. */
    private TcgTrade findTrade(Long tradeId) {
        return tcgTradeRepository.findById(tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND));
    }

    /* 히스토리를 저장합니다. */
    private void saveHistory(TcgTrade trade, TcgTradeRequest tradeRequest, String userUuid, String content) {
        tcgTradeHistoryRepository.save(
            new TcgTradeHistory(trade, tradeRequest, userUuid, content)
        );
    }

    /* 요청된 상태가 다음 값으로 유효한지 검증합니다. */
    private void validateRequestNextStatus(Integer status) {
        if (!status.equals(TcgTradeRequestStatus.REQUEST.getCode()) && 
            !status.equals(TcgTradeRequestStatus.PROCESS.getCode())) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "유효하지 않은 상태 값입니다.");
        }
    }

    /* 교환 글 소유권을 검증합니다. */
    private void validateIsTradeOwner(TcgTradeRequest tcgTradeRequest, String userUuid) {
        if (!tcgTradeRequest.getTrade().getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS);
        }
    }

    /* 현재 상태와 요청된 상태가 일치하는지 검증합니다. */
    private void validateCurrentStatus(TcgTradeRequest tcgTradeRequest, Integer requestedStatus) {
        if (!tcgTradeRequest.getStatus().equals(requestedStatus)) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, 
                "현재 상태와 요청된 상태가 일치하지 않습니다. 현재: " + tcgTradeRequest.getStatus() + ", 요청: " + requestedStatus);
        }
    }

    /* 안전하게 교환글의 다음 상태를 가져옵니다. */
    private Integer getNextTradeStatusSafely(Integer currentStatus) {
        try {
            return TradeStatus.getNextStatus(currentStatus);
        } catch (IllegalArgumentException e) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, e.getMessage());
        }
    }

    /* 안전하게 요청의 다음 상태를 가져옵니다. */
    private Integer getNextRequestStatusSafely(Integer currentStatus) {
        try {
            return TcgTradeRequestStatus.getNextStatus(currentStatus);
        } catch (IllegalArgumentException e) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, e.getMessage());
        }
    }

    /* 교환글 상태 변경 히스토리 내용을 생성합니다. */
    private String createStatusChangeHistoryContent(Integer nextStatus, String nickname) {
        if (nextStatus.equals(TcgTradeRequestStatus.PROCESS.getCode())) {
            return String.format("%s님이 교환을 진행 상태로 변경했습니다.", nickname);
        } else if (nextStatus.equals(TcgTradeRequestStatus.COMPLETE.getCode())) {
            return String.format("%s님이 교환을 완료했습니다.", nickname);
        } else {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "유효하지 않은 상태 값입니다.");
        }
    }

    /* 삭제 권한을 검증합니다. */
    private void validateDeletePermission(TcgTradeRequest tcgTradeRequest, String userUuid, Boolean isAdmin) {
        if (!isAdmin && !tcgTradeRequest.getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS);
        }
    }

    /* 삭제 가능한 상태인지 검증합니다. */
    private void validateDeletableStatus(TcgTradeRequest tcgTradeRequest) {
        if (tcgTradeRequest.getStatus().equals(TcgTradeRequestStatus.DELETE.getCode())) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_ALREADY_DELETED);
        }
        if (tcgTradeRequest.getStatus().equals(TcgTradeRequestStatus.COMPLETE.getCode())) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_ALREADY_COMPLETED);
        }
    }

    /* 삭제 히스토리 내용을 생성합니다. */
    private String createDeleteHistoryContent(String nickname, Boolean isAdmin) {
        return String.format("%s 님이 교환 요청을 취소했습니다.",
            isAdmin ? "관리자" : nickname);
    }

    /* 거래 횟수를 업데이트합니다. */
    private void updateTradeCount(String requestUserUuid, String tradeOwnerUuid) {
        // 업데이트할 UUID 리스트 생성 (중복 제거)
        List<String> uuidsToUpdate = requestUserUuid.equals(tradeOwnerUuid) 
            ? List.of(requestUserUuid)
            : List.of(requestUserUuid, tradeOwnerUuid);
        
        // 한 번에 조회
        List<TcgTradeUser> existingUsers = tcgTradeUserRepository.findAllById(uuidsToUpdate);
        
        // 존재하는 사용자들의 UUID 추출
        List<String> existingUuids = existingUsers.stream()
                .map(TcgTradeUser::getUuid)
                .toList();
        
        // 기존 사용자들의 거래 횟수 증가
        existingUsers.forEach(TcgTradeUser::incrementTradeCount);
        
        // 새로 생성해야 할 사용자들 추가
        List<TcgTradeUser> newUsers = uuidsToUpdate.stream()
                .filter(uuid -> !existingUuids.contains(uuid))
                .map(uuid -> {
                    TcgTradeUser newUser = new TcgTradeUser(uuid);
                    newUser.incrementTradeCount();
                    return newUser;
                })
                .toList();
        
        // 기존 사용자 업데이트
        if (!existingUsers.isEmpty()) {
            tcgTradeUserRepository.saveAll(existingUsers);
        }
        
        // 새 사용자 생성
        if (!newUsers.isEmpty()) {
            tcgTradeUserRepository.saveAll(newUsers);
        }
    }
}
