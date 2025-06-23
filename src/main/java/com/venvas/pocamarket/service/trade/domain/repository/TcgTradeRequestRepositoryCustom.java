package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestGetResponse;

import java.util.List;

public interface TcgTradeRequestRepositoryCustom {
    
    /**
     * 거래 ID로 교환 요청 목록을 TcgTradeUser와 함께 조회합니다.
     */
    List<TcgTradeRequestGetResponse> findTradeRequestsWithTradeUser(Long tradeId, String userUuid, Boolean isAdmin);
} 