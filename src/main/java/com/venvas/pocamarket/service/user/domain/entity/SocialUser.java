package com.venvas.pocamarket.service.user.domain.entity;

import com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 소셜 로그인 사용자 정보를 관리하는 엔티티
 * 기존 User 엔티티와 연결되어 소셜 로그인 관련 정보를 저장
 */
@Entity
@Table(name = "social_user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_social_user_provider_id", columnNames = {"provider", "provider_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long id;
    
    /**
     * 연결된 사용자 UUID
     */
    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;
    
    /**
     * OAuth2 제공자 타입 (google, naver, kakao 등)
     */
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;
    
    /**
     * OAuth2 제공자에서 제공하는 사용자 고유 ID
     */
    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;
    
    /**
     * 소셜 로그인 제공자에서 제공하는 이메일
     */
    @Column(name = "provider_email", length = 100)
    private String providerEmail;
    
    /**
     * 소셜 로그인 제공자에서 제공하는 이름
     */
    @Column(name = "provider_name", length = 50)
    private String providerName;
    
    /**
     * 소셜 로그인 제공자에서 제공하는 닉네임
     */
    @Column(name = "provider_nickname", length = 50)
    private String providerNickname;
    
    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    /**
     * 이메일 인증 여부
     */
    @Builder.Default
    @Column(name = "email_verified", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean emailVerified = false;
    
    /**
     * 소셜 로그인 제공자에서 제공하는 추가 정보
     * JSON 형태로 저장
     */
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    /**
     * 연결 상태 (활성/비활성)
     */
    @Builder.Default
    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;
    
    /**
     * 최근 로그인 시각
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    /**
     * 생성 시각
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정 시각
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * OAuth2UserInfoDto로부터 SocialUser 엔티티 생성
     * 
     * @param uuid 연결될 사용자 UUID
     * @param userInfo OAuth2 사용자 정보
     * @return 생성된 SocialUser 엔티티
     */
    public static SocialUser createFromOAuth2UserInfo(String uuid, OAuth2UserInfoDto userInfo) {
        return SocialUser.builder()
                .uuid(uuid)
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .providerEmail(userInfo.getEmail())
                .providerName(userInfo.getName())
                .providerNickname(userInfo.getNickname())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .emailVerified(userInfo.isEmailVerified())
                .rawData(userInfo.getRawData())
                .isActive(true)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 소셜 로그인 정보 업데이트
     * 
     * @param userInfo 업데이트할 OAuth2 사용자 정보
     */
    public void updateFromOAuth2UserInfo(OAuth2UserInfoDto userInfo) {
        this.providerEmail = userInfo.getEmail();
        this.providerName = userInfo.getName();
        this.providerNickname = userInfo.getNickname();
        this.profileImageUrl = userInfo.getProfileImageUrl();
        this.emailVerified = userInfo.isEmailVerified();
        this.rawData = userInfo.getRawData();
        this.lastLoginAt = LocalDateTime.now();
    }
    
    /**
     * 소셜 로그인 연결 해제
     */
    public void disconnect() {
        this.isActive = false;
    }
    
    /**
     * 소셜 로그인 연결 재활성화
     */
    public void reconnect() {
        this.isActive = true;
        this.lastLoginAt = LocalDateTime.now();
    }
    
    /**
     * 이메일 인증 여부를 안전하게 반환
     * 
     * @return 이메일 인증 여부
     */
    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }
    
    /**
     * 연결 상태를 안전하게 반환
     * 
     * @return 연결 상태
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }
} 