package com.venvas.pocamarket.service.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 상태를 나타내는 열거형
 * 각 상태는 고유한 코드 값과 설명을 가짐
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE(1, "활성 상태"),
    INACTIVE(0, "비활성 상태"),
    SUSPENDED(2, "일시 정지"),
    DELETED(9, "탈퇴");

    private final int code;
    private final String description;

    /**
     * 코드 값으로 UserStatus 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 UserStatus, 없으면 null
     */
    /**
     * 코드 값으로 UserStatus 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 UserStatus, 없으면 null
     */
    public static UserStatus fromCode(int code) {
        for (UserStatus status : UserStatus.values()) {
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
    public static Integer toCode(UserStatus status) {
        if (status == null) {
            return ACTIVE.getCode(); // 기본값 설정
        }
        return status.getCode();
    }
    
    /**
     * DB에서 읽어온 값을 열거형으로 변환
     * 
     * @param code DB에서 읽어온 코드 값
     * @return 열거형 값, 일치하는 값이 없으면 기본값
     */
    public static UserStatus fromDbCode(Integer code) {
        if (code == null) {
            return ACTIVE; // 기본값 설정
        }
        
        UserStatus status = fromCode(code);
        return status != null ? status : ACTIVE; // 알 수 없는 코드는 기본값으로
    }
}
