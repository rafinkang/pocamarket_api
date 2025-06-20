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

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginAt;

    /**
     * User 엔티티로부터 UserInfoResponse DTO를 생성하는 정적 팩토리 메소드
     * 
     * @param user 사용자 엔티티
     * @return UserInfoResponse DTO
     */
    public static UserInfoResponse from(User user) {
        // 마지막 로그인 시간을 계산 (로그인 성공 이력 중 가장 최근의 것)
        Optional<LocalDateTime> lastLoginDate = user.getLoginHistories().stream()
                .filter(UserLoginHistory::getSuccess) // 성공한 로그인만 필터링
                .map(UserLoginHistory::getLoginAt) // LocalDateTime 타입으로 변경됨
                .max(LocalDateTime::compareTo); // LocalDateTime::compareTo 사용

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
                .createdAt(user.getCreatedAt()) // LocalDateTime 타입으로 변경됨
                .updatedAt(user.getUpdatedAt()) // LocalDateTime 타입으로 변경됨
                .lastLoginAt(lastLoginDate.orElse(null)) // Optional<LocalDateTime> 처리
                .build();
    }
}
