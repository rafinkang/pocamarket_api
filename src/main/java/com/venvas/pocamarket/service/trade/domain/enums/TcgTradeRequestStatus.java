package com.venvas.pocamarket.service.trade.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 교환 요청 상태 열거형
 */
@Getter
@RequiredArgsConstructor
public enum TcgTradeRequestStatus {
    DELETE(0, "교환 삭제"),
    REQUEST(1, "교환 요청"),
    PROCESS(2, "교환 진행"),
    COMPLETE(3, "교환 완료");

    private final Integer code;
    private final String description;

    /**
     * DB 코드로부터 열거형 값을 찾습니다.
     * 
     * @param code DB에 저장된 코드
     * @return 해당하는 열거형 값
     * @throws IllegalArgumentException 유효하지 않은 코드인 경우
     */
    public static TcgTradeRequestStatus fromDbCode(Integer code) {
        if (code == null) {
            return REQUEST; // 기본값
        }
        
        for (TcgTradeRequestStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("유효하지 않은 교환 요청 상태 코드: " + code);
    }

    /**
     * 열거형 값을 DB 코드로 변환합니다.
     * 
     * @param status 열거형 값
     * @return DB 코드
     */
    public static Integer toCode(TcgTradeRequestStatus status) {
        return status != null ? status.getCode() : REQUEST.getCode();
    }

    /**
     * 현재 상태에서 다음 단계로 진행할 수 있는 상태를 반환합니다.
     * 1(REQUEST) → 2(PROGRESS)
     * 2(PROGRESS) → 3(COMPLETE)
     */
    public static Integer getNextStatus(Integer currentStatus) {
        if (currentStatus == null) {
            throw new IllegalArgumentException("현재 상태가 null 입니다.");
        }
        
        if (currentStatus.equals(REQUEST.getCode())) {
            return PROCESS.getCode(); // 1 → 2
        } else if (currentStatus.equals(PROCESS.getCode())) {
            return COMPLETE.getCode(); // 2 → 3
        } else {
            throw new IllegalArgumentException("더 이상 진행할 수 없는 상태입니다. 현재 상태: " + currentStatus);
        }
    }
} 