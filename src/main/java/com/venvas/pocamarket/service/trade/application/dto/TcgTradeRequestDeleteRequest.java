package com.venvas.pocamarket.service.trade.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TcgTradeRequestDeleteRequest {

    @NotNull(message = "교환 요청 id 값은 필수 입니다.")
    private Long tcgTradeRequestId;
}
