package com.venvas.pocamarket.service.trade.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 거래 상태 enum
 */
@Getter
@RequiredArgsConstructor
public enum TradeStatus {
    DELETED(0, "거래 삭제"),
    REQUEST(1, "거래 요청"),
    SELECT(2, "거래 선택"),
    PROCESS(3, "거래 진행중"),
    COMPLETE(4, "거래 완료");

    private final Integer code;
    private final String description;

    /**
     * 코드 값으로 TradeStatus 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 TradeStatus, 없으면 null
     */
    public static TradeStatus fromCode(int code) {
        for (TradeStatus status : TradeStatus.values()) {
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
    public static Integer toCode(TradeStatus status) {
        if (status == null) {
            return REQUEST.getCode(); // 기본값 설정
        }
        return status.getCode();
    }
    
    /**
     * DB에서 읽어온 값을 열거형으로 변환
     * 
     * @param code DB에서 읽어온 코드 값
     * @return 열거형 값, 일치하는 값이 없으면 기본값
     */
    public static TradeStatus fromDbCode(Integer code) {
        if (code == null) {
            return REQUEST; // 기본값 설정
        }
        
        TradeStatus status = fromCode(code);
        return status != null ? status : REQUEST; // 알 수 없는 코드는 기본값으로
    }
}
