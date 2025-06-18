package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeListRequest {

    @Size(max = 20, message = "카드 코드는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]*$", message = "카드 코드는 영문, 숫자, 하이픈(-)만 사용 가능합니다.")
    private String myCardCode;

    @Size(max = 10, message = "원하는 카드 코드는 최대 10개까지 가능합니다.")
    private List<@Size(max = 20, message = "카드 코드는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]*$", message = "카드 코드는 영문, 숫자, 하이픈(-)만 사용 가능합니다.")
            String> wantCardCode;

    @Size(max = 15, message = "필터값은 15자를 넘을수 없습니다.")
    @Pattern(regexp = "^[a-z\\-]*$", message = "필터 옵션은 영문, 하이픈(-)만 사용 가능합니다.")
    private String filterOption;
}
