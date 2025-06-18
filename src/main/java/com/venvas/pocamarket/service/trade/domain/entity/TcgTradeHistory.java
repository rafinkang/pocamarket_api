package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 거래 히스토리 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tcg_trade_history")
public class TcgTradeHistory {
    /** 히스토리 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_history_id")
    private Long id;

    /** 거래번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private TcgTrade trade;

    /** 교환요청번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_request_id")
    private TcgTradeRequest tradeRequest;

    /** 히스토리 주체 유저 */
    @Column(name = "uuid", length = 50)
    private String uuid;

    /** 내용 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 생성시간 */
    @Column(name = "create_at")
    private LocalDateTime createAt;
} 