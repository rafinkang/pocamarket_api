package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TcgTradeRequestCreateRequest {

    @NotBlank(message = "친구 코드는 필수 입력 항목입니다")
    @Pattern(regexp = "^[0-9]{16}$", message = "친구 코드는 16자리 숫자만 입력 가능합니다.")
    private String tcgCode;

    @NotBlank(message = "카드 코드는 필수 입력 항목입니다")
    @Size(max = 20, message = "카드 코드는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "카드 코드는 영문, 숫자, 하이픈(-)만 사용 가능합니다.")
    private String cardCode;

    @NotBlank(message = "카드 이름은 필수 입력 항목입니다")
    @Size(max = 20, message = "카드 이름은 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9\\s\\-]+$", message = "카드 이름은 한글, 영문, 숫자, 하이픈(-), 띄어쓰기만 사용 가능합니다.")
    private String cardName;
}
