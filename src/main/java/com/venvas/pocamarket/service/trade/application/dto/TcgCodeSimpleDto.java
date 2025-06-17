package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TcgCodeSimpleDto {

    private Long tcgCodeId;

    @Pattern(regexp = "^[0-9]{16}$", message = "친구 코드는 16자리 숫자만 입력 가능합니다.")
    private String tcgCode;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9\\s,.?!\\-_()\\[\\]]*$", message = "한글, 영문, 숫자, 공백 및 일부 특수문자(.,?!-_()[])만 입력 가능합니다.")
    private String memo;
}
