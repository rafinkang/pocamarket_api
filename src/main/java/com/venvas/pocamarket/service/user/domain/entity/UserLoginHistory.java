package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 로그인 이력을 관리하는 엔티티
 * 로그인 시도 시간, IP 주소, 브라우저 정보, 성공/실패 여부 등을 기록
 */
@Entity
@Table(name = "user_login_history")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginHistory {
    /**
     * 로그인 이력 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 로그인한 사용자 정보 (FK)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_user_login_history"))
    private User user;

    /**
     * 로그인 시도 일시
     */
    @CreationTimestamp
    @Column(name = "login_at", nullable = false, updatable = false)
    private LocalDateTime loginAt;

    /**
     * 로그인 시도 IP 주소 (IPv6 포함)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 로그인 시도 브라우저/앱 정보
     */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    /**
     * 로그인 성공/실패 여부
     * true: 성공, false: 실패
     */
    @Column(name = "success", nullable = false)
    private Boolean success = true;

    /**
     * 로그인 실패 사유
     * 로그인 실패 시에만 기록
     */
    @Column(name = "fail_reason", length = 100)
    private String failReason;
}