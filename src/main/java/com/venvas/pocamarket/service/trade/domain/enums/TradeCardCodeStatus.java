package com.venvas.pocamarket.service.trade.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 거래 카드 코드 타입 enum
 */
@Getter
@RequiredArgsConstructor
public enum TradeCardCodeStatus {
    MY(1, "내 카드"),
    WANT(2, "원하는 카드");

    private final Integer code;
    private final String description;

    /**
     * 코드 값으로 TradeCardCodeStatus 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 TradeCardCodeStatus, 없으면 null
     */
    public static TradeCardCodeStatus fromCode(int code) {
        for (TradeCardCodeStatus status : TradeCardCodeStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * DB에 저장할 값으로 변환
     * 
     * @param status 변환할 상태
     * @return DB에 저장할 정수 값
     */
    public static Integer toCode(TradeCardCodeStatus status) {
        if (status == null) {
            return MY.getCode(); // 기본값 설정
        }
        return status.getCode();
    }
    
    /**
     * DB에서 읽어온 값을 열거형으로 변환
     * 
     * @param code DB에서 읽어온 코드 값
     * @return 열거형 값, 일치하는 값이 없으면 기본값
     */
    public static TradeCardCodeStatus fromDbCode(Integer code) {
        if (code == null) {
            return MY; // 기본값 설정
        }
        
        TradeCardCodeStatus status = fromCode(code);
        return status != null ? status : MY; // 알 수 없는 코드는 기본값으로
    }
}
