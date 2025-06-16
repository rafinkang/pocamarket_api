package com.venvas.pocamarket.service.user.domain.entity;

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
@Table(name = "user_report")
public class UserReport {
    /** 신고번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    /** 레퍼런스 PK 값 */
    @Column(name = "ref_id")
    private Long refId;

    /** 레퍼런스 타입 */
    @Column(name = "ref_type", length = 50)
    private String refType;

    /** 링크 */
    @Column(name = "link", length = 200)
    private String link;

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