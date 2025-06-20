package com.venvas.pocamarket.service.user.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.entity.UserLoginHistory;
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
    private String accessToken;
    private String refreshToken;
    private ResponseCookie accessTokenCookie;
    private ResponseCookie refreshTokenCookie;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginAt;

    /**
     * User 엔티티로부터 응답 객체 생성
     * 
     * @param user  사용자 엔티티
     * @param token 인증 토큰
     * @return 로그인 응답 DTO
     */
    public static UserLoginResponse from(User user, String accessToken, String refreshToken,
            ResponseCookie accessTokenCookie, ResponseCookie refreshTokenCookie) {
        // 마지막 로그인 시간 계산 (성공한 로그인 중 가장 최근)
        Optional<LocalDateTime> lastLoginDate = user.getLoginHistories().stream()
                .filter(UserLoginHistory::getSuccess)
                .map(UserLoginHistory::getLoginAt)
                .max(LocalDateTime::compareTo);

        return UserLoginResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .status(user.getStatus())
                .grade(user.getGrade())
                .emailVerified(user.isEmailVerified())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenCookie(accessTokenCookie)
                .refreshTokenCookie(refreshTokenCookie)
                .lastLoginAt(lastLoginDate.orElse(null))
                .build();
    }
}
