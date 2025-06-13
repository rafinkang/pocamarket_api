package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 유저 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tcg_trade_user")
public class TcgTradeUser {
    /** UUID */
    @Id
    @Column(name = "uuid", length = 50)
    private String uuid;

    /** 거래 성사 횟수 */
    @Column(name = "trade_count")
    private Integer tradeCount = 0;

    /** 신고당한횟수 */
    @Column(name = "report_count")
    private Integer reportCount = 0;

    /** 경험치 */
    @Column(name = "exp")
    private Integer exp = 0;

    /** 포인트 */
    @Column(name = "point")
    private Integer point = 0;
} 