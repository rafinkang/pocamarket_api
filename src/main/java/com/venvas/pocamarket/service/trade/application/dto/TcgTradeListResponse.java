package com.venvas.pocamarket.service.trade.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeListResponse {

    private Long tradeId;

    private String nickname;

    private Integer status;

    private LocalDateTime created_at;

    private CardData myCardInfo;

    private List<CardData> wantCardInfo = new ArrayList<>();

    private List<TcgTradeCardCodeDto> tradeCardCodeList = new ArrayList<>();

    @QueryProjection
    public TcgTradeListResponse(Long tradeId, String nickname, Integer status, LocalDateTime created_at, List<TcgTradeCardCodeDto> tradeCardCodeList) {
        this.tradeId = tradeId;
        this.nickname = nickname;
        this.status = status;
        this.created_at = created_at;
        this.tradeCardCodeList = tradeCardCodeList;
    }

    public void updateMyCardInfo(String cardCode, String cardName) {
        this.myCardInfo = new CardData(cardCode, cardName);
    }

    public void updateWantCardInfo(String cardCode, String cardName) {
        if(wantCardInfo == null) {
            wantCardInfo = new ArrayList<>();
        }
        wantCardInfo.add(new CardData(cardCode, cardName));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardData {
        private String cardCode;
        private String cardName;
    }
}
