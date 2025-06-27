package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 카드 교환 요청 생성 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeCreateRequest {

    @NotBlank(message = "내 카드 코드는 필수 입력 항목입니다.")
    @Size(max = 20, message = "카드 코드는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "카드 코드는 영문, 숫자, 하이픈(-)만 사용 가능합니다.")
    private String myCardCode;

    @NotEmpty(message = "원하는 카드 코드는 최소 1개 이상 있어야 합니다.")
    @Size(max = 10, message = "원하는 카드 코드는 최대 10개까지 가능합니다.")
    private List<@NotBlank(message = "카드 코드는 공백일 수 없습니다.")
            @Size(max = 20, message = "카드 코드는 20자를 초과할 수 없습니다.")
            @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "카드 코드는 영문, 숫자, 하이픈(-)만 사용 가능합니다.")
            String> wantCardCode;

    @NotBlank(message = "친구 코드는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[0-9]{16}$", message = "친구 코드는 16자리 숫자만 입력 가능합니다.")
    private String tcgCode;
} 