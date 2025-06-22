package com.venvas.pocamarket.service.trade.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.enums.TradeStatus;

import lombok.Getter;

@Getter
public class TcgTradeDetailResponse {
    private Long tradeId;
    private String tcgCode;
    private String nickname;
    private TradeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TcgTradeDetailCardCodeDto myCard;
    private List<TcgTradeDetailCardCodeDto> wantCards;
    private Boolean isMy;

    public TcgTradeDetailResponse(TcgTrade tcgTrade, TcgTradeDetailCardCodeDto myCard, List<TcgTradeDetailCardCodeDto> wantCards, boolean isMy) {
        this.tradeId = tcgTrade.getId();
        this.tcgCode = tcgTrade.getTcgCode();
        this.nickname = tcgTrade.getNickname();
        this.status = TradeStatus.fromDbCode(tcgTrade.getStatus());
        this.createdAt = tcgTrade.getCreatedAt();
        this.updatedAt = tcgTrade.getUpdatedAt();
        this.myCard = myCard;
        this.wantCards = wantCards;
        this.isMy = isMy;
    }
}
