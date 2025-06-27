package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리프레쉬 토큰 엔티티
 * 사용자의 리프레쉬 토큰 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "refresh_token", uniqueConstraints = {
        @UniqueConstraint(name = "uk_refresh_token_token", columnNames = "token")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean revoked = false;

    /**
     * 리프레쉬 토큰 생성
     *
     * @param uuid      사용자 UUID
     * @param token     리프레쉬 토큰 문자열
     * @param issuedAt  발급 시각
     * @param expiresAt 만료 시각
     * @return 생성된 리프레쉬 토큰 엔티티
     */
    @Builder
    public RefreshToken(String uuid, String token, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.uuid = uuid;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    /**
     * 토큰 강제 만료 처리 (로그아웃, 탈퇴 등)
     */
    public void revokeToken() {
        this.revoked = true;
    }

    /**
     * 토큰이 유효한지 확인
     * 
     * @param currentTime 현재 시간
     * @return 유효 여부
     */
    public boolean isValid(LocalDateTime currentTime) {
        return !revoked && expiresAt.isAfter(currentTime);
    }
}
