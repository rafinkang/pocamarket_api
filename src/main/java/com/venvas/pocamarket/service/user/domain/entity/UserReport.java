package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.venvas.pocamarket.service.user.application.dto.UserReportRequest;

/**
 * 거래 신고 엔티티
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_report")
@Builder
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

    /** 신고한 시점(상태값) */
    @Column(name = "ref_status", nullable = false)
    private Integer refStatus;

    /** 링크 */
    @Column(name = "link", length = 200)
    private String link;

    /** 신고자 */
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    /** 신고사유 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 신고시간 */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

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
    private LocalDateTime resultAt;

    @PrePersist
    public void prePersist() {
        this.status = this.status == null ? 1 : this.status;
    }
    
    public static UserReport createFromRequest(UserReportRequest request) {
        UserReport report = UserReport.builder()
            .refId(request.getRefId())
            .refType(request.getRefType())
            .link(request.getLink())
            .uuid(request.getUuid())
            .content(request.getContent())
            .refStatus(request.getRefStatus())
            .build();

        return report;
    }
} 