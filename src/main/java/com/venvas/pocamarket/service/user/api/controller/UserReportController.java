package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
import com.venvas.pocamarket.service.user.application.dto.UserReportRequest;
import com.venvas.pocamarket.service.user.application.dto.UserReportResponse;
import com.venvas.pocamarket.service.user.application.service.UserReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "UserReport-API", description = "유저 신고 관련 API")
@RestController
@RequestMapping("/api/user-report")
@RequiredArgsConstructor
public class UserReportController {
    private final UserReportService userReportService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> saveUserReport(@RequestBody UserReportRequest request, @AuthenticationPrincipal UserDetailDto userDetailDto) {
        log.info("신고 접수 시작: {}", request);
        try {
            request.setUuid(userDetailDto.getUuid());
            userReportService.saveReport(request);
            return ResponseEntity.ok(ApiResponse.success(true, "신고 접수가 완료되었습니다."));
        } catch (Exception e) {
            log.error("신고 접수 중 알 수 없는 에러 발생: {}", e.getMessage(), e);
            ApiResponse<Object> errorResponse = ApiResponse.error("처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "500");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getUserReport(@PageableDefault(size = 30) Pageable pageable, @AuthenticationPrincipal UserDetailDto userDetailDto) {
       try {
            Page<UserReportResponse> response =  userReportService.getReportsByUuid(pageable, userDetailDto.getUuid());
            return ResponseEntity.ok(ApiResponse.success(response, "ok"));
       } catch (Exception e) {
            log.error("신고 조회 중 알 수 없는 에러 발생: {}", e.getMessage(), e);
            ApiResponse<Object> errorResponse = ApiResponse.error("처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "500");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
       } 
    }
}
