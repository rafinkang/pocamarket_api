package com.venvas.pocamarket.service.trade.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private List<CardData> wantCardInfo;

    @QueryProjection
    public TcgTradeListResponse(Long tradeId, String nickname, Integer status, LocalDateTime created_at) {
        this.tradeId = tradeId;
        this.nickname = nickname;
        this.status = status;
        this.created_at = created_at;
    }

    public void updateCardInfo(CardData myCardInfo, List<CardData> wantCardInfo) {
        this.myCardInfo = myCardInfo;
        this.wantCardInfo = wantCardInfo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardData {
        private String myCardCode;
        private String myCardName;
    }
}
