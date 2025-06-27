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

    public TcgTradeUser(String uuid) {
        this.uuid = uuid;
        this.tradeCount = 0;
        this.reportCount = 0;
        this.exp = 0;
        this.point = 0;
    }

    public void incrementTradeCount() {
        this.tradeCount++;
    }

    public void addPoint(Integer point) {
        this.point += point;
    }

    public void addExp(Integer exp) {
        this.exp += exp;
    }

    /**
     * 신고 횟수 증가
     */
    public void incrementReportCount() {
        this.reportCount++;
    }

    /**
     * 신고 횟수 감소
     */
    public void decrementReportCount() {
        this.reportCount--;
        if(this.reportCount < 0) {
            this.reportCount = 0;
        }
    }
} 