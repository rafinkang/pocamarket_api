package com.venvas.pocamarket.service.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 등급을 나타내는 열거형
 * 각 등급은 고유한 코드 값과 설명을 가짐
 */
@Getter
@RequiredArgsConstructor
public enum UserGrade {
    LV01(1, "몬스터 볼"),
    LV02(2, "슈퍼 볼"),
    LV03(3, "하이퍼 볼"),
    LV04(4, "마스터 볼"),
    LV05(5, "프리미어 볼"),
    LV06(6, "럭셔리 볼"),
    LV10(10, "오박사"),
    ADMIN(99, "관리자");

    private final int code;
    private final String description;

    /**
     * 코드 값으로 UserGrade 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 UserGrade, 없으면 null
     */
    /**
     * 코드 값으로 UserGrade 찾기
     * 
     * @param code 찾을 코드 값
     * @return 해당 코드 값에 맞는 UserGrade, 없으면 null
     */
    public static UserGrade fromCode(int code) {
        for (UserGrade grade : UserGrade.values()) {
            if (grade.getCode() == code) {
                return grade;
            }
        }
        return null;
    }
    
    /**
     * DB에 저장할 값으로 변환
     * 
     * @param grade 변환할 등급
     * @return DB에 저장할 정수 값
     */
    public static Integer toCode(UserGrade grade) {
        if (grade == null) {
            return LV01.getCode(); // 기본값 설정
        }
        return grade.getCode();
    }
    
    /**
     * DB에서 읽어온 값을 열거형으로 변환
     * 
     * @param code DB에서 읽어온 코드 값
     * @return 열거형 값, 일치하는 값이 없으면 기본값
     */
    public static UserGrade fromDbCode(Integer code) {
        if (code == null) {
            return LV01; // 기본값 설정
        }
        
        UserGrade grade = fromCode(code);
        return grade != null ? grade : LV01; // 알 수 없는 코드는 기본값으로
    }
}
