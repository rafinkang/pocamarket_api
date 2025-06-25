package com.venvas.pocamarket.service.trade.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeListResponse {

    private Long tradeId;

    private String nickname;

    private Integer status;

    private LocalDateTime updated_at;

    private Boolean isMyList = false;

    private CardData myCardInfo;

    private List<CardData> wantCardInfo = new ArrayList<>();

    /**
     * TcgTradeListDto를 TcgTradeListResponse로 변환하는 생성자
     */
    public TcgTradeListResponse(TcgTradeListDto dto) {
        this.tradeId = dto.getTradeId();
        this.nickname = dto.getNickname();
        this.status = dto.getStatus();
        this.updated_at = dto.getUpdated_at();
        this.isMyList = dto.getIsMyList();
        
        // myCardInfo 변환
        this.myCardInfo = dto.getMyCardInfo() != null ? 
            new CardData(
                dto.getMyCardInfo().getCardCode(),
                dto.getMyCardInfo().getCardName(),
                dto.getMyCardInfo().getCardPackSet()
            ) : null;
            
        // wantCardInfo 변환
        this.wantCardInfo = dto.getWantCardInfo().stream()
            .map(cardData -> new CardData(
                cardData.getCardCode(),
                cardData.getCardName(),
                cardData.getCardPackSet()
            ))
            .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardData {
        private String cardCode;
        private String cardName;
        private String cardPackSet;
    }
}
