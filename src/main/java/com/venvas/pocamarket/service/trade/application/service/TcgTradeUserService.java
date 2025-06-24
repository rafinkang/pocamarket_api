package com.venvas.pocamarket.service.trade.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.venvas.pocamarket.service.trade.application.dto.TcgMyInfoResponse;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeUser;
import com.venvas.pocamarket.service.trade.domain.enums.TcgTradeRequestStatus;
import com.venvas.pocamarket.service.trade.domain.enums.TradeStatus;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeRequestRepository;
import com.venvas.pocamarket.service.trade.domain.repository.TcgTradeUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TcgTradeUserService {

    private final TcgTradeUserRepository tcgTradeUserRepository;
    private final TcgTradeRepository tcgTradeRepository;
    private final TcgTradeRequestRepository tcgTradeRequestRepository;

    /**
     * 사용자의 TCG 거래 정보를 조회합니다.
     * 
     * @param userUuid 사용자 UUID
     * @return TCG 거래 정보 응답 DTO
     */
    public TcgMyInfoResponse getMyTcgTradeInfo(String userUuid) {
        TcgTradeUser tcgTradeUser = tcgTradeUserRepository.findByUuid(userUuid)
                .orElseGet(() -> createDefaultTcgTradeUser(userUuid));

        Integer tradingCount = tcgTradeRepository.countByUuidAndStatusIn(userUuid,
                List.of(TradeStatus.REQUEST.getCode(), TradeStatus.PROCESS.getCode(), TradeStatus.SELECT.getCode()));
        Integer requestCount = tcgTradeRequestRepository.countByUuidAndTradeStatusIn(userUuid,
                List.of(TcgTradeRequestStatus.REQUEST.getCode(), TcgTradeRequestStatus.PROCESS.getCode()));

        return new TcgMyInfoResponse(tcgTradeUser, tradingCount, requestCount);
    }

    /**
     * 기본값을 가진 TcgTradeUser 객체를 생성합니다.
     * 
     * @param userUuid 사용자 UUID
     * @return 기본값이 설정된 TcgTradeUser 객체
     */
    private TcgTradeUser createDefaultTcgTradeUser(String userUuid) {
        TcgTradeUser defaultUser = new TcgTradeUser();
        // TcgTradeUser 엔티티에서 이미 기본값이 0으로 설정되어 있어서
        // 별도로 setter나 생성자로 값을 설정할 필요가 없습니다.
        return defaultUser;
    }
}
