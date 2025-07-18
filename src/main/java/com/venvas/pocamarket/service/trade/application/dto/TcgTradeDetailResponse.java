package com.venvas.pocamarket.service.trade.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;

import lombok.Getter;

@Getter
public class TcgTradeDetailResponse {
    private Long tradeId;
    private String tcgCode;
    private String nickname;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TcgTradeDetailCardCodeDto myCard;
    private List<TcgTradeDetailCardCodeDto> wantCards;
    private Boolean isMy;
    private TcgMyInfoResponse userInfo;


    public TcgTradeDetailResponse(TcgTrade tcgTrade, TcgTradeDetailCardCodeDto myCard, List<TcgTradeDetailCardCodeDto> wantCards, boolean isMy, TcgMyInfoResponse userInfo) {
        this.tradeId = tcgTrade.getId();
        this.tcgCode = tcgTrade.getTcgCode();
        this.nickname = tcgTrade.getNickname();
        this.status = tcgTrade.getStatus();
        this.createdAt = tcgTrade.getCreatedAt();
        this.updatedAt = tcgTrade.getUpdatedAt();
        this.myCard = myCard;
        this.wantCards = wantCards;
        this.isMy = isMy;
        this.userInfo = userInfo;
    }
}
