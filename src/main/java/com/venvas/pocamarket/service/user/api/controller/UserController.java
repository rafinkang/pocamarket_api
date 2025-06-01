package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.application.dto.UserInfoResponse;
import com.venvas.pocamarket.service.user.application.dto.UserLoginRequest;
import com.venvas.pocamarket.service.user.application.dto.UserLoginResponse;
import com.venvas.pocamarket.service.user.application.dto.UserUpdateRequest;
import com.venvas.pocamarket.service.user.application.service.UserService;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



/**
 * 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 사용자 생성, 조회, 수정 등의 엔드포인트를 제공
 */
@Slf4j
@Tag(name = "User-API", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 새로운 사용자 생성
     * 
     * @param request 사용자 생성 요청 데이터
     * @return 생성된 사용자 정보와 성공 메시지
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserCreateRequest request) {
        log.info("사용자 생성 요청: loginId={}", request.getLoginId());
        User createdUser = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(createdUser, "사용자가 성공적으로 생성되었습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * 
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo() {
        // 인증 된 사용자 ID를 가져오는 로직이 필요합니다.
        // 실제 서비스에서는 Spring Security 또는 JWT 토큰에서 사용자 ID를 추출합니다.
        // 데모 목적으로 임의로 1L을 사용합니다.
        Long userId = 1L;
        
        log.info("사용자 정보 조회: userId={}", userId);
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        
        return ResponseEntity.ok(ApiResponse.success(userInfo, "사용자 정보를 성공적으로 가져왔습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 업데이트합니다.
     * 
     * @param request 사용자 정보 업데이트 요청
     * @return 업데이트된 사용자 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateMyInfo(@Valid @RequestBody UserUpdateRequest request) {
        // 인증 된 사용자 ID를 가져오는 로직이 필요합니다.
        // 실제 서비스에서는 Spring Security 또는 JWT 토큰에서 사용자 ID를 추출합니다.
        // 데모 목적으로 임의로 1L을 사용합니다.
        Long userId = 1L;
        
        log.info("사용자 정보 업데이트: userId={}", userId);
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(updatedUserInfo, "사용자 정보가 성공적으로 업데이트되었습니다."));
    }

    /**
     * 현재 로그인한 사용자의 계정을 삭제(탈퇴)합니다.
     * 
     * @param request 계정 삭제 요청(비밀번호 포함)
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyInfo(@RequestBody Map<String, String> request) {
        // 인증 된 사용자 ID를 가져오는 로직이 필요합니다.
        // 실제 서비스에서는 Spring Security 또는 JWT 토큰에서 사용자 ID를 추출합니다.
        // 데모 목적으로 임의로 1L을 사용합니다.
        Long userId = 1L;
        String password = request.get("password");
        
        if (password == null || password.isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
        
        log.info("사용자 계정 삭제: userId={}", userId);
        userService.deleteUserAccount(userId, password);
        
        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 성공적으로 처리되었습니다."));
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
