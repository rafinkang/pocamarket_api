package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TcgCodeSimpleDto {

    private Long tcgCodeId;
    
    @Pattern(regexp = "^[0-9]{16}$", message = "친구 코드는 16자리 숫자만 입력 가능합니다.")
    private String tcgCode;

    @Size(min = 1, max = 15, message = "메모는 최소 한 글자 이상, 열다섯 글자 이하입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9\\s,.?!\\-_()\\[\\]]*$", message = "한글, 영문, 숫자, 공백 및 일부 특수문자(.,?!-_()[])만 입력 가능합니다.")
    private String memo;
}
