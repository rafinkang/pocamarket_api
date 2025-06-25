package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.pokemon.application.dto.TradeListCardDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import com.venvas.pocamarket.service.trade.application.dto.*;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeHistory;
import com.venvas.pocamarket.service.trade.domain.enums.TcgTradeRequestStatus;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;
import com.venvas.pocamarket.service.trade.domain.enums.TradeStatus;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeException;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeCardCodeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeHistoryRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRequestRepository;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 카드 교환 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TcgTradeService {

    private final UserRepository userRepository;
    private final TcgTradeRepository tcgTradeRepository;
    private final TcgTradeCardCodeRepository tcgTradeCardCodeRepository;
    private final PokemonCardRepository pokemonCardRepository;
    private final TcgTradeRequestRepository tcgTradeRequestRepository;
    private final TcgTradeHistoryRepository tcgTradeHistoryRepository;

    /**
     * 카드 교환 요청을 생성합니다.
     * 
     * @param request  교환 요청 데이터 (DTO)
     * @param userUuid 요청자 UUID
     * @return 처리 결과 메시지
     */
    @Transactional
    public Boolean createTrade(TcgTradeCreateRequest request, String userUuid) {
        log.info("카드 교환 요청 처리 시작: userUuid={}, myCard={}, wantCards={}, tcgCode={}",
                userUuid, request.getMyCardCode(), request.getWantCardCode(), request.getTcgCode());

        // 1. 중복 카드 검증 및 중복 제거
        TcgTradeCreateRequest processedRequest = validateAndProcessDuplicateCardCodes(request);
        
        // 2. 유저 정보 가져오기
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        
        // 3. TcgTrade 테이블 insert
        TcgTrade savedTrade = saveTcgTrade(processedRequest, user);
        
        // 4. TcgTrade 테이블 result id 를 가지고 TcgTradeCardCode 테이블 insert 내카드 1, 원하는 카드 2 코드 저장
        saveTcgTradeCardCodes(savedTrade, processedRequest);
        
        return true;
    }

    /**
     * 카드 교환 요청을 수정합니다.
     * 
     * @param tradeId  교환 요청 ID
     * @param request  교환 요청 데이터 (DTO)
     * @param userUuid 요청자 UUID
     * @return 처리 결과 메시지
     */
    @Transactional
    public Boolean putTcgTrade(Long tradeId, TcgTradeCreateRequest request, String userUuid) {
        // 0. 요청 데이터 검증 및 중복 카드 제거
        TcgTradeCreateRequest processedRequest = validateAndProcessDuplicateCardCodes(request);
        
        // 1. 거래 요청 업데이트
        TcgTrade tcgTrade = tcgTradeRepository.findById(tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND));

        // 권한 검증: 작성자만 수정 가능
        if (!tcgTrade.getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS);
        }

        // 거래 상태에 따라 수정 가능 여부 검증
        if (tcgTrade.getStatus() > TradeStatus.REQUEST.getCode()) {
            throw new TcgTradeException(TcgTradeErrorCode.TRADE_ALREADY_PROCESS);
        }

        // 2. 기존 거래 요청 카드 데이터 가져오기
        List<TcgTradeCardCode> existingCardCodes = tcgTradeCardCodeRepository.findByTrade(tcgTrade);
        
        // 3. 기존 거래 요청 카드 데이터와 새로운 거래 요청 카드 데이터 비교
        boolean hasChanges = hasCardCodeChanges(existingCardCodes, processedRequest);
        
        // 4. 기존 거래 요청 카드 데이터와 새로운 거래 요청 카드 데이터 비교 후 차이가 있으면 삭제 후 새로운 거래 요청 카드 데이터 저장
        if (hasChanges) {
            // 기존 카드 코드 데이터 삭제
            tcgTradeCardCodeRepository.deleteByTrade(tcgTrade);
            // 새로운 카드 코드 데이터 저장
            saveTcgTradeCardCodes(tcgTrade, processedRequest);
        }
        
        // TcgTrade 정보 업데이트 (tcgCode)
        if (!tcgTrade.getTcgCode().equals(processedRequest.getTcgCode())) {
            tcgTrade.updateTcgCode(processedRequest.getTcgCode());
        }

        return true;
    }

    public Page<TcgTradeListResponse> getTradeList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin, boolean isMy) {
        String myCardCode = request.getMyCardCode();
        List<String> wantCardCode = distinctCards(request.getWantCardCode());

        // 원하는 카드 최대 길이 체크
        maxChekCards(wantCardCode);

        // 원하는 카드 중복 제거
        ProcessDuplicateCardCodes(myCardCode, wantCardCode);

        TcgTradeListRequest tcgTradeRequest = new TcgTradeListRequest(myCardCode, wantCardCode, request.getStatus());

//     TcgTrade 검색 isMy가 true면 userUuid 값 전달
        Page<TcgTradeListDto> tcgTradeListDto = tcgTradeRepository.searchFilterList(tcgTradeRequest, pageable, isMy ? userUuid : null, isAdmin);

        tcgTradeListDto.getContent().forEach((TcgTradeListDto response) -> {
            if(userUuid != null && !userUuid.isBlank()) {
                response.updateIsMyList(response.getUuid().equals(userUuid));
            }
        });

        // 가져온 리스트에서 card code 추출
        List<String> allCardCodes = tcgTradeListDto.getContent().stream()
            .filter(tcgTrade -> !tcgTrade.getTradeCardCodeList().isEmpty())
            .flatMap(tcgTrade -> tcgTrade.getTradeCardCodeList().stream())
            .map(TcgTradeCardCodeDto::getCardCode)
            .distinct()
            .toList();

        // 카드 코드 및 한글 이름 가져오기
        List<TradeListCardDto> findCardList = pokemonCardRepository.findByCodeInGetTradeListCardInfo(allCardCodes);

        // 카드 코드를 Map으로 변환하여 빠른 조회가 가능하도록 함
        Map<String, TradeListCardDto> cardCodeMap = findCardList.stream()
                .collect(Collectors.toMap(TradeListCardDto::getCode, card -> card));

        // 가져온 카드 데이터 response에 주입
        tcgTradeListDto.getContent().stream()
                .filter(tcgTrade -> !tcgTrade.getTradeCardCodeList().isEmpty())
                .forEach(tcgTrade -> {
                    List<TcgTradeCardCodeDto> cardList = tcgTrade.getTradeCardCodeList();
                    for (TcgTradeCardCodeDto codeType : cardList) {
                        TradeListCardDto cardInfo = cardCodeMap.get(codeType.getCardCode());
                        if (cardInfo != null) {
                            if (codeType.getType() == 1) {
                                tcgTrade.updateMyCardInfo(cardInfo);
                            } else {
                                tcgTrade.updateWantCardInfo(cardInfo);
                            }
                        }
                    }
                    cardList.clear();
                });

        // TcgTradeListDto를 TcgTradeListResponse로 변환
        return tcgTradeListDto.map(TcgTradeListResponse::new);
    }

    @Transactional
    public Boolean tcgTradeRefreshList(Long tradeId, TcgTradeRefreshRequest request, String userUuid) {

        // uuid 있는지 체크
        if(userUuid == null || userUuid.isBlank()) throw new UserException(UserErrorCode.USER_NOT_FOUND);

        // 클라이언트에서 온 정보 확인
        if(! TradeStatus.REQUEST.getCode().equals(request.getStatus())) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "교환 글의 상태가 유효하지 않습니다.");
        }
        try {
            LocalDateTime requestTime = LocalDateTime.parse(request.getUpdatedAt());
            Duration duration = Duration.between(requestTime, LocalDateTime.now());
            if (duration.toHours() < 1) {
                throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "갱신은 최소 1시간마다 가능합니다.");
            }
        } catch (DateTimeParseException e) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "날짜 형식이 올바르지 않습니다.");
        }

        // code 값으로 tcgTrade 가져오기
        TcgTrade tcgTrade = tcgTradeRepository.findById(tradeId).orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "유효한 id 값이 아닙니다."));

        // db에서 가져온 정보랑 비교
        if(! tcgTrade.getUuid().equals(userUuid)) {
            // listUuid, userUuid랑 같은지 비교
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "유저 uuid가 교환 글과 일치하지 않습니다.");
        } else if(! TradeStatus.REQUEST.getCode().equals(tcgTrade.getStatus())) {
            // 상태 값이 요청 받는 중인 글인지 확인
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "교환 글의 상태가 유효하지 않습니다.");
        } else if(Duration.between(tcgTrade.getUpdatedAt(), LocalDateTime.now()).toHours() < 1) {
            // 카드에 저장된 update_at과 비교
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_REQUEST_DATA, "갱신은 최소 1시간마다 가능합니다..");
        }
        tcgTrade.refresh();

        return true;
    }

    /**
     * 카드 중복 검증 및 중복 카드 제거 처리
     */
    private TcgTradeCreateRequest validateAndProcessDuplicateCardCodes(TcgTradeCreateRequest request) {
        String myCardCode = request.getMyCardCode();
        List<String> wantCardCodes = request.getWantCardCode();

        // 원하는 카드 목록에서 중복 제거
        List<String> distinctWantCards = distinctCards(wantCardCodes);

        // 내 카드와 원하는 카드 중복 검증
        ProcessDuplicateCardCodes(myCardCode, distinctWantCards);

        // 중복 제거된 요청 객체 반환
        return new TcgTradeCreateRequest(myCardCode, distinctWantCards, request.getTcgCode());
    }

    private void maxChekCards(List<String> cards) {
        int MAX_SIZE = 3;
        if(cards.size() > MAX_SIZE) {
            throw new TcgTradeException(TcgTradeErrorCode.TOO_MANY_WANT_CARDS);
        }
    }

    private List<String> distinctCards(List<String> cards) {
        if(cards == null) {
            return new ArrayList<>();
        }
        return cards.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void ProcessDuplicateCardCodes(String myCard, List<String> wantCards) {
        if (wantCards.contains(myCard)) {
            throw new TcgTradeException(
                    TcgTradeErrorCode.INVALID_WANT_CARD_LIST,
                    "내 카드와 원하는 카드가 동일할 수 없습니다.");
        }
    }
    
    /**
     * TcgTrade 엔티티 저장
     */
    private TcgTrade saveTcgTrade(TcgTradeCreateRequest request, User user) {
        TcgTrade tcgTrade = new TcgTrade(
                request.getTcgCode(),
                user.getUuid(),
                user.getNickname(),
                TradeStatus.REQUEST.getCode()
        );
        
        TcgTrade savedTrade = tcgTradeRepository.save(tcgTrade);        
        return savedTrade;
    }
    
    /**
     * TcgTradeCardCode 엔티티들 저장
     */
    private void saveTcgTradeCardCodes(TcgTrade trade, TcgTradeCreateRequest request) {
        List<TcgTradeCardCode> cardCodes = new ArrayList<>();
        
        // 내 카드 저장 (type = 1)
        cardCodes.add(new TcgTradeCardCode(trade, request.getMyCardCode(), TradeCardCodeStatus.MY.getCode()));
        
        // 원하는 카드들 저장 (type = 2)
        for (String wantCard : request.getWantCardCode()) {
            cardCodes.add(new TcgTradeCardCode(trade, wantCard, TradeCardCodeStatus.WANT.getCode()));
        }
        
        tcgTradeCardCodeRepository.saveAll(cardCodes);
    }
    
    /**
     * 기존 카드 코드와 새로운 카드 코드 변경사항 비교
     * 
     * @param existingCardCodes 기존 카드 코드 목록
     * @param request 새로운 요청 데이터
     * @return 변경사항 존재 여부
     */
    private boolean hasCardCodeChanges(List<TcgTradeCardCode> existingCardCodes, TcgTradeCreateRequest request) {
        // 기존 카드 코드를 type별로 분류
        String existingMyCard = null;
        List<String> existingWantCards = new ArrayList<>();
        
        for (TcgTradeCardCode cardCode : existingCardCodes) {
            if (cardCode.getType() == TradeCardCodeStatus.MY.getCode()) {
                existingMyCard = cardCode.getCardCode();
            } else if (cardCode.getType() == TradeCardCodeStatus.WANT.getCode()) {
                existingWantCards.add(cardCode.getCardCode());
            }
        }
        
        // 내 카드 비교
        if (!request.getMyCardCode().equals(existingMyCard)) {
            return true;
        }
        
        // 원하는 카드 개수 비교
        if (request.getWantCardCode().size() != existingWantCards.size()) {
            return true;
        }
        
        // 원하는 카드 내용 비교 (순서 무관)
        List<String> sortedNewWantCards = new ArrayList<>(request.getWantCardCode());
        List<String> sortedExistingWantCards = new ArrayList<>(existingWantCards);
        sortedNewWantCards.sort(String::compareTo);
        sortedExistingWantCards.sort(String::compareTo);
        
        return !sortedNewWantCards.equals(sortedExistingWantCards);
    }
    
    /**
     * TcgTradeDetail 조회
     */
    @Transactional(readOnly = true)
    public TcgTradeDetailResponse getTcgTradeById(Long tradeId, UserDetailDto currentUser) {
        // 1. TcgTrade와 TcgTradeCardCode 목록 조회 (기존과 동일)
        TcgTrade tcgTrade = tcgTradeRepository.findByIdWithCardCodes(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));

        // 2. 모든 카드 코드 추출 (기존과 동일)
        List<String> cardCodes = tcgTrade.getTcgTradeCardCodes().stream()
                .map(TcgTradeCardCode::getCardCode)
                .distinct()
                .collect(Collectors.toList());

        // 3. PokemonCard 상세 정보 한번에 조회 (기존과 동일)
        Map<String, PokemonCard> pokemonCardMap = pokemonCardRepository.findByCodeIn(cardCodes).stream()
                .collect(Collectors.toMap(PokemonCard::getCode, card -> card));

        // 4. '내 카드'와 '원하는 카드' DTO 리스트 생성 (기존과 동일)
        List<TcgTradeDetailCardCodeDto> myCardsList = tcgTrade.getTcgTradeCardCodes().stream()
                .filter(tradeCard -> tradeCard.getType() == TradeCardCodeStatus.MY.getCode())
                .map(tradeCard -> new TcgTradeDetailCardCodeDto(tradeCard, pokemonCardMap.get(tradeCard.getCardCode())))
                .collect(Collectors.toList());

        List<TcgTradeDetailCardCodeDto> wantCardsList = tcgTrade.getTcgTradeCardCodes().stream()
                .filter(tradeCard -> tradeCard.getType() == TradeCardCodeStatus.WANT.getCode())
                .map(tradeCard -> new TcgTradeDetailCardCodeDto(tradeCard, pokemonCardMap.get(tradeCard.getCardCode())))
                .collect(Collectors.toList());

        // '내 카드'가 없을 경우를 대비한 방어 코드
        TcgTradeDetailCardCodeDto myCard = myCardsList.isEmpty() ? null : myCardsList.get(0);

        boolean isMy = false;

        if (currentUser != null) {
            String currentUserUuid = currentUser.getUuid();
            isMy = currentUserUuid != null && tcgTrade.getUuid().equals(currentUserUuid);
        }

        // 5. 최종 DTO 생성 및 반환 (public 생성자 직접 호출)
        return new TcgTradeDetailResponse(tcgTrade, myCard, wantCardsList, isMy);
    }

    /**
     * 거래 글 삭제
     * 
     * @param tradeId 거래 글 ID
     * @param userUuid 사용자 UUID
     * @return 삭제 결과
     */
    @Transactional
    public Boolean deleteTrade(Long tradeId, String userUuid) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 1. 거래 글 상태 DELETED 로 변경
        TcgTrade tcgTrade = tcgTradeRepository.findById(tradeId)
                .orElseThrow(() -> new TcgTradeException(TcgTradeErrorCode.TRADE_NOT_FOUND));

        if(!tcgTrade.getUuid().equals(userUuid)) {
            throw new TcgTradeException(TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS);
        }

        tcgTrade.updateStatus(TradeStatus.DELETED.getCode());
        tcgTradeRepository.save(tcgTrade);
        
        // 2. 해당 거래에 대한 요청들 상태 DELETED 로 변경
        tcgTradeRequestRepository.updateStatusByTradeId(tradeId, TcgTradeRequestStatus.DELETE.getCode());

        // 3. 삭제에 대한 히스토리 로그 추가
        String historyContent = String.format("%s 님이 거래 글을 삭제했습니다.", user.getNickname());
        tcgTradeHistoryRepository.save(new TcgTradeHistory(tcgTrade, null, userUuid, historyContent));

        return true;
    }
}