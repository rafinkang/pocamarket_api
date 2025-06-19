package com.venvas.pocamarket.service.trade.application.dto;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;

import lombok.Getter;

@Getter
public class TcgTradeDetailCardCodeDto {
    private final String cardCode;
    private final TradeCardCodeStatus type;

    public TcgTradeDetailCardCodeDto(TcgTradeCardCode entity) {
        this.cardCode = entity.getCardCode();
        this.type = TradeCardCodeStatus.fromDbCode(entity.getType());
    }
}
