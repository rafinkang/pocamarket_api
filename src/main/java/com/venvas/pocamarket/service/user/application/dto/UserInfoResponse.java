package com.venvas.pocamarket.service.user.application.dto;

import java.time.LocalDateTime;

import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
public class UserInfoResponse {
    private String uuid;
    private String loginId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String profileImageUrl;
    private UserStatus status;
    private UserGrade grade;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    /**
     * User 엔티티로부터 UserInfoResponse DTO를 생성하는 정적 팩토리 메소드
     * 
     * @param user 사용자 엔티티
     * @return UserInfoResponse DTO
     */
    public static UserInfoResponse from(User user) {
        // 마지막 로그인 시간을 계산 (로그인 성공 이력 중 가장 최근의 것)
        LocalDateTime lastLoginTime = user.getLoginHistories().stream()
                .filter(history -> Boolean.TRUE.equals(history.getSuccess()))
                .map(history -> history.getLoginAt())
                .max(LocalDateTime::compareTo)
                .orElse(null);
            
        return UserInfoResponse.builder()
                .uuid(user.getUuid())
                .loginId(user.getLoginId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .status(user.getStatus())
                .grade(user.getGrade())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(lastLoginTime)
                .build();
    }
}
