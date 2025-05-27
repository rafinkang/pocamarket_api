package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.application.dto.UserLoginRequest;
import com.venvas.pocamarket.service.user.application.dto.UserLoginResponse;
import com.venvas.pocamarket.service.user.application.service.UserService;
import com.venvas.pocamarket.service.user.domain.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 사용자 생성, 조회, 수정 등의 엔드포인트를 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 새로운 사용자 생성
     * 
     * @param request 사용자 생성 요청 데이터
     * @return 생성된 사용자 정보와 성공 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("사용자 생성 요청: loginId={}", request.getLoginId());
        User createdUser = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(createdUser, "사용자가 성공적으로 생성되었습니다."));
    }
    
    /**
     * 사용자 로그인
     * 
     * @param request 로그인 요청 데이터
     * @param httpRequest HTTP 요청 객체 (클라이언트 정보 추출용)
     * @return 로그인 결과 정보와 성공 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {
        
        // 클라이언트 정보 추출 및 설정
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        
        request.setIpAddress(ipAddress);
        request.setUserAgent(userAgent);
        
        log.info("로그인 요청: loginId={}, ip={}", request.getLoginId(), ipAddress);
        UserLoginResponse loginResponse = userService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "로그인에 성공하였습니다."));
    }
}
