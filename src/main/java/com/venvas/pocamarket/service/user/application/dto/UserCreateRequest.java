package com.venvas.pocamarket.service.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "password") // 로그에 비밀번호가 노출되지 않도록 제외
public class UserCreateRequest {
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 4, max = 15, message = "로그인 ID는 4~15자 사이여야 합니다")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$", 
             message = "비밀번호는 숫자, 영문자, 특수문자를 각각 1개 이상 포함해야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 10, message = "이름은 10자 이하여야 합니다")
    private String name;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 8, message = "닉네임은 8자 이하여야 합니다")
    private String nickname;

    // 이메일은 필수 항목이 아님
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Pattern(regexp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,}$", message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 30, message = "이메일은 30자 이하여야 합니다")
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;
}
