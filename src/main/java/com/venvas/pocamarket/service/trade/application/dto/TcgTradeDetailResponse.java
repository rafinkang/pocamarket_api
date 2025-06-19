package com.venvas.pocamarket.service.trade.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;
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

    public static TcgTradeDetailResponse from(TcgTrade tcgTrade) {
        List<TcgTradeDetailCardCodeDto> myCardList = tcgTrade.getTcgTradeCardCodes().stream()
                .filter(card -> TradeCardCodeStatus.fromDbCode(card.getType()) == TradeCardCodeStatus.MY)
                .map(TcgTradeDetailCardCodeDto::new)
                .collect(Collectors.toList());

        List<TcgTradeDetailCardCodeDto> wantCardList = tcgTrade.getTcgTradeCardCodes().stream()
                .filter(card -> TradeCardCodeStatus.fromDbCode(card.getType()) == TradeCardCodeStatus.WANT)
                .map(TcgTradeDetailCardCodeDto::new)
                .collect(Collectors.toList());

        return new TcgTradeDetailResponse(
            tcgTrade.getId(),
            tcgTrade.getTcgCode(),
            tcgTrade.getNickname(),
            tcgTrade.getStatus(),
            tcgTrade.getCreatedAt(),
            tcgTrade.getUpdatedAt(),
            myCardList.get(0),
            wantCardList
        );
    }
    
    // fron 메소드를 위한 private 생성자
    private TcgTradeDetailResponse(Long id, String tcgCode, String nickname, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt, TcgTradeDetailCardCodeDto myCard, List<TcgTradeDetailCardCodeDto> wantCards) {
        this.tradeId = id;
        this.tcgCode = tcgCode;
        this.nickname = nickname;
        this.status = TradeStatus.fromDbCode(status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.myCard = myCard;
        this.wantCards = wantCards;
    }
}
