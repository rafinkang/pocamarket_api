package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 거래 신고 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tcg_trade_report")
public class TcgTradeReport {
    /** 신고번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_report_id")
    private Long id;

    /** 교환요청번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_request_id", nullable = false)
    private TcgTradeRequest tradeRequest;

    /** 거래번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private TcgTrade trade;

    /** 신고자 */
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    /** 신고사유 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 신고한 시점(상태값) */
    @Column(name = "trade_status", nullable = false)
    private Integer tradeStatus;

    /** 신고시간 */
    @Column(name = "created_at")
    private Date createdAt;

    /** 상태값 1: 신고 2: 처리됨 3:보류 */
    @Column(name = "status")
    private Integer status = 1;

    /** 처리 대응 */
    @Column(name = "report_result", columnDefinition = "TEXT")
    private String reportResult;

    /** 대응한 관리자 */
    @Column(name = "admin_uuid", length = 50)
    private String adminUuid;

    /** 처리 대응 시간 */
    @Column(name = "result_at")
    private Date resultAt;
} 