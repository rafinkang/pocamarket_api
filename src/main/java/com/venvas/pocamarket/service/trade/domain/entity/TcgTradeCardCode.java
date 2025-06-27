package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 카드 코드 엔티티
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tcg_trade_card_code")
public class TcgTradeCardCode {
    /** 거래 카드 코드 id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_card_code_id")
    private Long id;

    /** 거래번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private TcgTrade trade;

    /** 카드코드 */
    @Column(name = "card_code", nullable = false, length = 50)
    private String cardCode;

    /** 1: 내카드, 2: 원하는 카드 */
    @Column(name = "type")
    private Integer type = 1;
    
    /**
     * 거래 카드 코드 엔티티 생성 (ID 제외)
     */
    public TcgTradeCardCode(TcgTrade trade, String cardCode, Integer type) {
        this.trade = trade;
        this.cardCode = cardCode;
        this.type = type;
    }
} 