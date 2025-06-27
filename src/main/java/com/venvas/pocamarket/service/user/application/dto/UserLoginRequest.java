package com.venvas.pocamarket.service.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 사용자 로그인 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    @NotBlank(message = "로그인 ID는 필수입니다.")
    private String loginId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    
    // 로그인 추적을 위한 정보
    private String ipAddress;
    private String userAgent;
}
