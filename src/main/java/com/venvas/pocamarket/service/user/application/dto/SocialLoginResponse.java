package com.venvas.pocamarket.service.user.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.entity.UserLoginHistory;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 소셜 로그인 응답 DTO
 * 소셜 로그인 성공 시 반환되는 정보를 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    
    /**
     * 사용자 기본 정보
     */
    private Long userId;
    private String uuid;
    private String loginId;
    private String nickname;
    private String email;
    private UserStatus status;
    private UserGrade grade;
    private String gradeDesc;
    private boolean emailVerified;
    private String profileImageUrl;
    
    /**
     * 소셜 로그인 관련 정보
     */
    private String provider;
    private String providerId;
    private boolean isNewUser;
    
    /**
     * JWT 토큰 정보
     */
    private String accessToken;
    private String refreshToken;
    private ResponseCookie accessTokenCookie;
    private ResponseCookie refreshTokenCookie;
    
    /**
     * 로그인 시간 정보
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * User 엔티티와 SocialUser 엔티티로부터 응답 객체 생성
     * 
     * @param user 사용자 엔티티
     * @param socialUser 소셜 사용자 엔티티
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @param accessTokenCookie 액세스 토큰 쿠키
     * @param refreshTokenCookie 리프레시 토큰 쿠키
     * @param isNewUser 신규 사용자 여부
     * @return 소셜 로그인 응답 DTO
     */
    public static SocialLoginResponse from(User user, SocialUser socialUser, String accessToken, 
                                         String refreshToken, ResponseCookie accessTokenCookie, 
                                         ResponseCookie refreshTokenCookie, boolean isNewUser) {
        
        // 마지막 로그인 시간 계산 (성공한 로그인 중 가장 최근)
        Optional<LocalDateTime> lastLoginDate = user.getLoginHistories().stream()
                .filter(UserLoginHistory::getSuccess)
                .map(UserLoginHistory::getLoginAt)
                .max(LocalDateTime::compareTo);
        
        return SocialLoginResponse.builder()
                .userId(user.getId())
                .uuid(user.getUuid())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .status(user.getStatus())
                .grade(UserGrade.fromCode(user.getGradeCode()))
                .gradeDesc(UserGrade.toDesc(user.getGrade()))
                .emailVerified(user.isEmailVerified())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(socialUser.getProvider())
                .providerId(socialUser.getProviderId())
                .isNewUser(isNewUser)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenCookie(accessTokenCookie)
                .refreshTokenCookie(refreshTokenCookie)
                .lastLoginAt(lastLoginDate.orElse(null))
                .createdAt(user.getCreatedAt())
                .build();
    }
} 