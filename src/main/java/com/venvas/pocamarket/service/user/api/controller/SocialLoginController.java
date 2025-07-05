package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginRequest;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginResponse;
import com.venvas.pocamarket.service.user.application.service.SocialLoginService;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 소셜 로그인 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 구글, 네이버, 카카오톡 등의 소셜 로그인 엔드포인트를 제공
 */
@Slf4j
@Tag(name = "Social-Login-API", description = "소셜 로그인 관련 API")
@RestController
@RequestMapping("/auth/social")
@RequiredArgsConstructor
public class SocialLoginController {
    
    private final SocialLoginService socialLoginService;
    
    /**
     * 소셜 로그인 처리
     * OAuth2 인증 코드를 통해 소셜 로그인을 수행
     * 
     * @param provider 소셜 로그인 제공자 (google, naver, kakao 등)
     * @param request 소셜 로그인 요청 데이터
     * @param httpRequest HTTP 요청 객체 (클라이언트 정보 추출용)
     * @param httpResponse HTTP 응답 객체 (쿠키 설정용)
     * @return 소셜 로그인 결과 정보와 성공 메시지
     */
    @Operation(summary = "소셜 로그인", description = "OAuth2 인증 코드를 통해 소셜 로그인을 수행합니다.")
    @PostMapping("/login/{provider}")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
            @Parameter(description = "소셜 로그인 제공자", example = "google")
            @PathVariable("provider") String provider,
            @Valid @RequestBody SocialLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        // 클라이언트 정보 추출 및 설정
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        
        request.setProvider(provider);
        request.setIpAddress(ipAddress);
        request.setUserAgent(userAgent);

        log.info("소셜 로그인 요청: provider={}, redirectUri={}, ip={}", 
            provider, request.getRedirectUri(), ipAddress);
        
        // 소셜 로그인 처리
        SocialLoginResponse loginResponse = socialLoginService.processSocialLogin(request);
        
        // 쿠키에 토큰 추가
        CookieUtil.addCookie(httpResponse, loginResponse.getAccessTokenCookie());
        CookieUtil.addCookie(httpResponse, loginResponse.getRefreshTokenCookie());
        
        // 응답 데이터 구성 (민감한 정보 제외)
        SocialLoginResponse response = SocialLoginResponse.builder()
                // .userId(loginResponse.getUserId())
                // .uuid(loginResponse.getUuid())
                // .loginId(loginResponse.getLoginId())
                .nickname(loginResponse.getNickname())
                // .email(loginResponse.getEmail())
                .status(loginResponse.getStatus())
                .grade(loginResponse.getGrade())
                .gradeDesc(UserGrade.toDesc(loginResponse.getGrade()))
                // .emailVerified(loginResponse.isEmailVerified())
                .profileImageUrl(loginResponse.getProfileImageUrl())
                // .provider(loginResponse.getProvider())
                // .providerId(loginResponse.getProviderId())
                // .isNewUser(loginResponse.isNewUser())
                .lastLoginAt(loginResponse.getLastLoginAt())
                // .createdAt(loginResponse.getCreatedAt())
                .build();
        
        String message = loginResponse.isNewUser() ? 
            "소셜 로그인 회원가입이 완료되었습니다." : 
            "소셜 로그인에 성공하였습니다.";
        
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }
    
    /**
     * 지원하는 소셜 로그인 제공자 목록 조회
     * 
     * @return 지원하는 제공자 목록
     */
    @Operation(summary = "지원 제공자 목록", description = "지원하는 소셜 로그인 제공자 목록을 조회합니다.")
    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedProviders() {
        log.info("지원하는 소셜 로그인 제공자 목록 조회");
        
        List<String> providers = socialLoginService.getSupportedProviders();
        
        return ResponseEntity.ok(ApiResponse.success(providers, "지원하는 소셜 로그인 제공자 목록을 조회했습니다."));
    }
    
    /**
     * 특정 제공자 지원 여부 확인
     * 
     * @param provider 확인할 제공자
     * @return 지원 여부
     */
    @Operation(summary = "제공자 지원 여부 확인", description = "특정 소셜 로그인 제공자의 지원 여부를 확인합니다.")
    @GetMapping("/providers/{provider}/supported")
    public ResponseEntity<ApiResponse<Boolean>> isProviderSupported(
            @Parameter(description = "확인할 소셜 로그인 제공자", example = "google")
            @PathVariable("provider") String provider) {
        
        log.info("소셜 로그인 제공자 지원 여부 확인: provider={}", provider);
        
        boolean isSupported = socialLoginService.isProviderSupported(provider);
        
        String message = isSupported ? 
            "지원하는 소셜 로그인 제공자입니다." : 
            "지원하지 않는 소셜 로그인 제공자입니다.";
        
        return ResponseEntity.ok(ApiResponse.success(isSupported, message));
    }
} 