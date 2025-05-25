package com.venvas.pocamarket.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답을 표준화하기 위한 공통 응답 DTO
 * @param <T> 응답 데이터의 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    /**
     * 성공 응답 생성
     * @param data 응답 데이터
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Success", null);
    }

    /**
     * 성공 응답 생성 (커스텀 메시지 포함)
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    /**
     * 에러 응답 생성
     * @param message 에러 메시지
     * @param errorCode 에러 코드
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, null, message, errorCode);
    }
} 