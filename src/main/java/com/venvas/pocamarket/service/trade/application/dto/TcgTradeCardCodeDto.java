package com.venvas.pocamarket.service.trade.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TcgTradeCardCodeDto {

    private String cardCode;
    /** 1: 내카드, 2: 원하는 카드 */
    private Integer type;
}
