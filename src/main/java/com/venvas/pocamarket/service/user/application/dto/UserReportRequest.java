package com.venvas.pocamarket.service.user.application.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserReportRequest {
    private Long id;
    private Long refId; // 게시글 pk
    private String refType; // 레버런스 타입
    private Integer refStatus; // 거래 상태
    private String link; // url
    private String uuid; // 신고자 uuid
    private String content; // 내용
    private LocalDateTime createdAt; // 생성일
    private Integer status; // 처리 상태값
    private String reportResult; // 대응 결과
    private String adminUuid; // 관리자 uuid
    private LocalDateTime resultAt; // 처리일
}
