package com.venvas.pocamarket.service.user.application.dto;

import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 로그인 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private Long userId;
    private String loginId;
    private String nickname;
    private String email;
    private UserStatus status;
    private UserGrade grade;
    private boolean emailVerified;
    private String token; // JWT 토큰 (실제 인증 시스템에 따라 달라질 수 있음)
    private LocalDateTime lastLoginAt;
    
    /**
     * User 엔티티로부터 응답 객체 생성
     * 
     * @param user 사용자 엔티티
     * @param token 인증 토큰
     * @return 로그인 응답 DTO
     */
    public static UserLoginResponse from(User user, String token) {
        return UserLoginResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .status(user.getStatus())
                .grade(user.getGrade())
                .emailVerified(user.isEmailVerified())
                .token(token)
                .lastLoginAt(user.getLoginHistories().stream()
                        .filter(history -> Boolean.TRUE.equals(history.getSuccess()))
                        .map(history -> history.getLoginAt())
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .build();
    }
}
