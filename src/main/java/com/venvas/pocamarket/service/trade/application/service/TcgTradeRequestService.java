package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestCreateRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestDeleteRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestGetResponse;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeHistory;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeRequest;
import com.venvas.pocamarket.service.trade.domain.enums.TcgTradeRequestStatus;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeException;
import com.venvas.pocamarket.service.trade.domain.repository.TcgCodeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeHistoryRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRequestRepository;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TcgTradeRequestService {
    private final TcgCodeRepository tcgCodeRepository;
    private final TcgTradeRequestRepository tcgTradeRequestRepository;
    private final TcgTradeHistoryRepository tcgTradeHistoryRepository;
    private final TcgTradeRepository tcgTradeRepository;
    private final UserRepository userRepository;

    private final Integer STATUS_ACTIVE = 1;

    /**
     * 카드 교환 요청을 생성합니다.
     * 
     * @param tradeId 거래 ID
     * @param request 교환 요청 생성 DTO
     * @param userUuid 요청자 UUID
     * @return 처리 결과
     * @throws UserException 사용자 관련 예외
     * @throws TcgTradeException 교환 관련 예외
     */
    @Transactional
    public Boolean createTcgTradeRequest(Long tradeId, TcgTradeRequestCreateRequest request, String userUuid) {
        log.info("카드 교환 요청 생성 시작: tradeId={}, userUuid={}, tcgCode={}, cardCode={}", 
                tradeId, userUuid, request.getTcgCode(), request.getCardCode());

        // 1. userUuid가 값이 없다면 에러를 띄움
        if (userUuid == null || userUuid.isBlank()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        // 2. tcgCodeRepository에서 userUuid와 request안에 있는 tcgCode 값으로 조회해서 확인
        boolean tcgCodeExists = tcgCodeRepository.existsByUuidAndTcgCodeAndStatus(userUuid, request.getTcgCode(), STATUS_ACTIVE);
        
        if (!tcgCodeExists) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_TCG_CODE_FORMAT, "등록되지 않은 친구 코드입니다.");
        }

        // 교환 정보 조회
        TcgTrade trade = tcgTradeRepository.findById(tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND));

        // 사용자 정보 조회
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 3. TcgTradeRequest 객체를 만들어서 db에 저장
        TcgTradeRequest tradeRequest = new TcgTradeRequest(
                trade,
                userUuid,
                user.getNickname(),
                request.getTcgCode(),
                request.getCardCode(),
                TcgTradeRequestStatus.REQUEST.getCode()
        );
        
        TcgTradeRequest savedTradeRequest = tcgTradeRequestRepository.save(tradeRequest);

        // 4. TcgTradeHistory 객체를 만들어서 db에 저장
        String historyContent = String.format("%s (%s)님이 %s (%s)카드로 교환을 요청했습니다.",
                user.getNickname(), user.getUuid(), request.getCardName(), request.getCardCode());
        
        TcgTradeHistory tradeHistory = new TcgTradeHistory(
            trade,
            savedTradeRequest,
            userUuid,
            historyContent
        );
        
        TcgTradeHistory savedTradeHistory = tcgTradeHistoryRepository.save(tradeHistory);

        return true;
    }

    /**
     * 교환 요청 목록을 조회합니다.
     */
    public Page<TcgTradeRequestGetResponse> getTcgTradeRequestList(Long tradeId, String userUuid, Pageable pageable, Boolean isAdmin) {
        if (!tcgTradeRepository.existsById(tradeId)) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND);
        }

        return tcgTradeRequestRepository.findTradeRequestsWithTradeUser(tradeId, userUuid, pageable, isAdmin);
    }

    @Transactional
    public Boolean deleteTradeRequest(Long tradeId, TcgTradeRequestDeleteRequest request, String userUuid, Boolean isAdmin) {

        if (userUuid == null || userUuid.isBlank()) {
            log.warn("요청자 UUID가 없습니다.");
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        if (!tcgTradeRepository.existsById(tradeId)) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND);
        }

        // 교환 요청 조회 및 권한 검증
        TcgTradeRequest tcgTradeRequest = tcgTradeRequestRepository
                .findByIdAndTradeId(request.getTcgTradeRequestId(), tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_NOT_FOUND));

        // 관리자가 아닌 경우 본인 것만 삭제 가능
        if (!isAdmin && !tcgTradeRequest.getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS);
        }

        // 이미 삭제된 요청인지 확인
        if (tcgTradeRequest.getStatus().equals(TcgTradeRequestStatus.DELETE.getCode())) {
            log.warn("이미 삭제된 교환 요청입니다. tcgTradeRequestId: {}", request.getTcgTradeRequestId());
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_ALREADY_DELETED);
        }

        // 이미 완료된 요청인지 확인
        if (tcgTradeRequest.getStatus().equals(TcgTradeRequestStatus.COMPLETE.getCode())) {
            log.warn("이미 완료된 교환 요청은 삭제할 수 없습니다. tcgTradeRequestId: {}", request.getTcgTradeRequestId());
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_REQUEST_ALREADY_COMPLETED);
        }

        tcgTradeRequest.updateStatus(TcgTradeRequestStatus.DELETE.getCode());
        TcgTradeRequest savedTradeRequest = tcgTradeRequestRepository.save(tcgTradeRequest);

        // 삭제 히스토리 저장
        String historyContent = String.format("%s (%s)님이 교환 요청을 취소했습니다.", isAdmin ? "관리자" : tcgTradeRequest.getNickname(), userUuid);
        
        tcgTradeHistoryRepository.save(
            new TcgTradeHistory(
                tcgTradeRequest.getTrade(),
                savedTradeRequest,
                userUuid,
                historyContent
        ));
        return true;
    }
}
