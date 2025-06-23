package com.venvas.pocamarket.service.trade.application.dto;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeUser;

import lombok.Getter;

@Getter
public class TcgMyInfoResponse {

    // 진행중인 교환
    private Integer tradingCount;
    // 내가 신청한 교환
    private Integer requestCount;
    // 완료된 교환
    private Integer tradeCount;
    // 신고당한 횟수
    private Integer reportCount;
    // 경험치(거래점수)
    private Integer exp;
    // 포인트
    private Integer point;

    public TcgMyInfoResponse(TcgTradeUser tcgTradeUser, Integer tradingCount, Integer requestCount) {
        this.tradingCount = tradingCount;
        this.requestCount = requestCount;
        this.tradeCount = tcgTradeUser.getTradeCount();
        this.reportCount = tcgTradeUser.getReportCount();
        this.exp = tcgTradeUser.getExp();
        this.point = tcgTradeUser.getPoint();
    }
}
