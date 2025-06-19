package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.pokemon.application.dto.CardCodeName;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeCardCodeDto;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeCreateRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeDetailResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListResponse;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;
import com.venvas.pocamarket.service.trade.domain.enums.TradeStatus;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeException;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeCardCodeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRepository;
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

    public Page<TcgTradeListResponse> getTradeList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin) {
        String myCardCode = request.getMyCardCode();
        List<String> wantCardCode = distinctCards(request.getWantCardCode());

        ProcessDuplicateCardCodes(myCardCode, wantCardCode);

        TcgTradeListRequest tcgTradeRequest = new TcgTradeListRequest(myCardCode, wantCardCode, request.getFilterOption());

        // TcgTrade 검색
        Page<TcgTradeListResponse> tcgTradeListResponses = tcgTradeRepository.searchFilterList(tcgTradeRequest, pageable, userUuid, isAdmin);

        // 가져온 리스트에서 card code 추출
        List<String> allCardCodes = tcgTradeListResponses.getContent().stream()
            .filter(tcgTrade -> !tcgTrade.getTradeCardCodeList().isEmpty())
            .flatMap(tcgTrade -> tcgTrade.getTradeCardCodeList().stream())
            .map(TcgTradeCardCodeDto::getCardCode)
            .distinct()
            .toList();

        // 카드 코드 및 한글 이름 가져오기
        List<CardCodeName> findCardList = pokemonCardRepository.findByCodeInGetCodeAndNameKo(allCardCodes);

        // 카드 코드를 Map으로 변환하여 빠른 조회가 가능하도록 함
        Map<String, CardCodeName> cardCodeMap = findCardList.stream()
                .collect(Collectors.toMap(CardCodeName::getCode, card -> card));

        // 가져온 카드 데이터 response에 주입
        tcgTradeListResponses.getContent().stream()
                .filter(tcgTrade -> !tcgTrade.getTradeCardCodeList().isEmpty())
                .forEach(tcgTrade -> {
                    List<TcgTradeCardCodeDto> cardList = tcgTrade.getTradeCardCodeList();
                    for (TcgTradeCardCodeDto codeType : cardList) {
                        CardCodeName cardInfo = cardCodeMap.get(codeType.getCardCode());
                        if (cardInfo != null) {
                            if (codeType.getType() == 1) {
                                tcgTrade.updateMyCardInfo(cardInfo.getCode(), cardInfo.getNameKo());
                            } else {
                                tcgTrade.updateWantCardInfo(cardInfo.getCode(), cardInfo.getNameKo());
                            }
                        }
                    }
                    cardList.clear();
                });

        // TODO :: 교환 요청 건수

        return tcgTradeListResponses;
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

    private List<String> distinctCards(List<String> cards) {
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
     * TcgTradeDetail 조회
     */
    @Transactional(readOnly = true)
    public TcgTradeDetailResponse getTcgTradeById(Long tradeId) {
        TcgTrade tcgTrade = tcgTradeRepository
                .findByIdWithCardCodes(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교환을 찾을 수 없습니다."));
        return TcgTradeDetailResponse.from(tcgTrade);
    }
}