package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.*;
import com.venvas.pocamarket.service.user.application.service.UserService;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.JwtErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 사용자 생성, 조회, 수정 등의 엔드포인트를 제공
 */
@Slf4j
@Tag(name = "User-API", description = "유저 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 새로운 사용자 생성
     * 
     * @param request 사용자 생성 요청 데이터
     * @return 생성된 사용자 정보와 성공 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserCreateRequest request) {
        log.info("사용자 생성 요청: loginId={}", request.getLoginId());
        User createdUser = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(createdUser, "사용자가 성공적으로 생성되었습니다."));
    }

    /**
     * 사용자 로그인
     * 
     * @param request     로그인 요청 데이터
     * @param httpRequest HTTP 요청 객체 (클라이언트 정보 추출용)
     * @return 로그인 결과 정보와 성공 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        // 클라이언트 정보 추출 및 설정
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        request.setIpAddress(ipAddress);
        request.setUserAgent(userAgent);

        log.info("로그인 요청: loginId={}, ip={}", request.getLoginId(), ipAddress);
        UserLoginResponse loginResponse = userService.login(request);

        // 쿠키에 토큰 추가
        CookieUtil.addCookie(httpResponse, loginResponse.getAccessTokenCookie());
        CookieUtil.addCookie(httpResponse, loginResponse.getRefreshTokenCookie());

        UserLoginResponse response = UserLoginResponse.builder()
                .nickname(loginResponse.getNickname())
                .status(loginResponse.getStatus())
                .grade(loginResponse.getGrade())
                .lastLoginAt(loginResponse.getLastLoginAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "로그인에 성공하였습니다."));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<UserLoginResponse>> reissue(
            @RequestBody Map<String, String> request,
            HttpServletResponse httpResponse) {

        String refreshToken = request.get("refreshToken");
        log.info("refreshToken = {}", refreshToken);
        JwtErrorCode refreshTokenErrorCode = jwtTokenProvider.validateToken(refreshToken);
        log.info("refreshTokenErrorCode = {}", refreshTokenErrorCode);
        if (refreshTokenErrorCode == null) {
            String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider.getUuid(refreshToken), jwtTokenProvider.getGrade(refreshToken));
            ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
                (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true);

            CookieUtil.addCookie(httpResponse, accessTokenCookie);

            return ResponseEntity.ok(ApiResponse.success(null, "토큰 재발급에 성공하였습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("유효하지 않은 리프레쉬 토큰입니다.", "UNAUTHORIZED_REFRESH_TOKEN"));
        }
    }
    

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        CookieUtil.deleteCookie(httpResponse, JwtTokenProvider.ACCESS_TOKEN_NAME);
        CookieUtil.deleteCookie(httpResponse, JwtTokenProvider.REFRESH_TOKEN_NAME);

        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃에 성공하였습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * 
     * @return 사용자 정보
     */
    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
            @AuthenticationPrincipal UserDetailDto userDetailDto) {

        log.info("사용자 정보 조회: uuid={}", userDetailDto.getUuid());
        UserInfoResponse userInfo = userService.getUserInfo(userDetailDto.getUuid());

        return ResponseEntity.ok(ApiResponse.success(userInfo, "사용자 정보를 성공적으로 가져왔습니다."));
    }

    /**
     * 현재 로그인한 사용자의 정보를 업데이트합니다.
     * 
     * @param request 사용자 정보 업데이트 요청
     * @return 업데이트된 사용자 정보
     */
    @PutMapping("/user/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateMyInfo(@Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        String uuid = userDetailDto.getUuid();

        log.info("사용자 정보 업데이트: uuid={}", uuid);
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(uuid, request);

        return ResponseEntity.ok(ApiResponse.success(updatedUserInfo, "사용자 정보가 성공적으로 업데이트되었습니다."));
    }

    /**
     * 현재 로그인한 사용자의 계정을 삭제(탈퇴)합니다.
     * 
     * @param request 계정 삭제 요청(비밀번호 포함)
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/user/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyInfo(@RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        String uuid = userDetailDto.getUuid();
        String password = request.get("password");

        if (password == null || password.isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        log.info("사용자 계정 삭제: uuid={}", uuid);
        userService.deleteUserAccount(uuid, password);

        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 성공적으로 처리되었습니다."));
    }

    @GetMapping("/user/tokenTest")
    public ResponseEntity<ApiResponse<String>> tokenTest(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(ApiResponse.success("ok", "인증에 실패했습니다."));
        }
        UserDetailDto principal = (UserDetailDto) authentication.getPrincipal(); // user 정보
        Object credentials = authentication.getCredentials();// 비번 (JWT는 null)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 권한
        log.info("test UUID = {}", principal.getUuid());
        log.info("test credentials = {}", credentials);
        for (Object o : authorities.toArray()) {
            log.info("test authorities = {}", authorities);
        }
        return ResponseEntity.ok(ApiResponse.success("ok", "테스트 성공하였습니다."));
    }
}
