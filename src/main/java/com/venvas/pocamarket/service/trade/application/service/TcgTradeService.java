package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeCreateRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
     * 카드 중복 검증 및 중복 카드 제거 처리
     */
    private TcgTradeCreateRequest validateAndProcessDuplicateCardCodes(TcgTradeCreateRequest request) {
        String myCardCode = request.getMyCardCode();
        List<String> wantCardCodes = request.getWantCardCode();

        // 원하는 카드 목록에서 중복 제거
        List<String> distinctWantCards = wantCardCodes.stream()
                .distinct()
                .collect(Collectors.toList());

        // 내 카드와 원하는 카드 중복 검증
        if (distinctWantCards.contains(myCardCode)) {
            throw new TcgTradeException(
                    TcgTradeErrorCode.INVALID_WANT_CARD_LIST,
                    "내 카드와 원하는 카드가 동일할 수 없습니다.");
        }

        // 중복 제거된 요청 객체 반환
        return new TcgTradeCreateRequest(myCardCode, distinctWantCards, request.getTcgCode());
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

}