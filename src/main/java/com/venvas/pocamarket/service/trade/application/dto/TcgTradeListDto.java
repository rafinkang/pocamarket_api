package com.venvas.pocamarket.service.trade.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.venvas.pocamarket.service.pokemon.application.dto.TradeListCardDto;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListResponse.CardData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeListDto {

    private Long tradeId;

    private String nickname;

    private Integer status;

    private LocalDateTime updated_at;

    private String uuid;

    private Boolean isMyList = false;

    private CardData myCardInfo;

    private List<CardData> wantCardInfo = new ArrayList<>();

    private List<TcgTradeCardCodeDto> tradeCardCodeList = new ArrayList<>();

    @QueryProjection
    public TcgTradeListDto(Long tradeId, String nickname, Integer status, LocalDateTime updated_at, String uuid, List<TcgTradeCardCodeDto> tradeCardCodeList) {
        this.tradeId = tradeId;
        this.nickname = nickname;
        this.status = status;
        this.updated_at = updated_at;
        this.uuid = uuid;
        this.tradeCardCodeList = tradeCardCodeList;
    }

    public void updateMyCardInfo(TradeListCardDto cardInfo) {
        this.myCardInfo = new CardData(cardInfo.getCode(), cardInfo.getNameKo(), cardInfo.getPackSet());
    }

    public void updateWantCardInfo(TradeListCardDto cardInfo) {
        if(wantCardInfo == null) {
            wantCardInfo = new ArrayList<>();
        }
        wantCardInfo.add(new CardData(cardInfo.getCode(), cardInfo.getNameKo(), cardInfo.getPackSet()));
    }

    public void updateIsMyList(Boolean isMyList) {
        this.isMyList = isMyList;
    }
}
