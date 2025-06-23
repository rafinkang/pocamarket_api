package com.venvas.pocamarket.service.trade.application.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TcgTradeRequestGetResponse {

    private Long tradeRequestId;

    private Long tradeId;

    private String nickname;

    private String tcgCode;

    private String requestCardCode;

    private Integer status;

    private Boolean isMy;

    private LocalDateTime updatedAt;

    // TcgTradeUser 정보 추가
    private Integer tradeCount;

    private Integer reportCount;

    // PokemonCard 정보 추가
    private String cardNameKo;

    private String cardElement;

    private String cardPackSet;

    private String cardRarity;

    // QueryDSL에서 사용할 uuid 필드 (응답에는 포함되지 않음)
    @JsonIgnore
    private String requestUuid;

    /**
     * QueryDSL Projections에서 사용하는 생성자 (PokemonCard 정보 포함)
     */
    public TcgTradeRequestGetResponse(Long tradeRequestId, Long tradeId, String nickname, String tcgCode, 
                                    String requestCardCode, Integer status, String requestUuid, LocalDateTime updatedAt,
                                    Integer tradeCount, Integer reportCount,
                                    String cardNameKo, String cardElement, String cardPackSet, String cardRarity) {
        this.tradeRequestId = tradeRequestId;
        this.tradeId = tradeId;
        this.nickname = nickname;
        this.tcgCode = tcgCode;
        this.requestCardCode = requestCardCode;
        this.status = status;
        this.requestUuid = requestUuid;
        this.updatedAt = updatedAt;
        this.tradeCount = tradeCount;
        this.reportCount = reportCount;
        this.cardNameKo = cardNameKo;
        this.cardElement = cardElement;
        this.cardPackSet = cardPackSet;
        this.cardRarity = cardRarity;
        // isMy는 나중에 setMyFlag() 메서드로 설정
    }

    /**
     * isMy 값을 설정하는 헬퍼 메서드
     */
    public void setMyFlag(String userUuid) {
        this.isMy = this.requestUuid != null && this.requestUuid.equals(userUuid);
    }
}
