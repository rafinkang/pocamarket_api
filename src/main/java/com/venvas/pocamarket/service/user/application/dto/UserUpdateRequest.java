package com.venvas.pocamarket.service.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Getter
@Setter
public class UserUpdateRequest {
    
    @Size(max = 10, message = "이름은 2~10자 사이여야 합니다")
    private String name;

    @Size(max = 8, message = "닉네임은 2~8자 사이여야 합니다")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;
    
    private String profileImageUrl;
    
    // 비밀번호 변경을 위한 필드
    private String currentPassword;
    
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다")
    private String newPassword;

}
