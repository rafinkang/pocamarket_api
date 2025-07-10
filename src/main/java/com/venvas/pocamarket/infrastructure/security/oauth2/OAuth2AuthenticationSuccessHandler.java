package com.venvas.pocamarket.infrastructure.security.oauth2;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginResponse;
import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
import com.venvas.pocamarket.service.user.domain.repository.SocialUserRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 로그인 성공 처리 핸들러
 * 로그인 성공 시 JWT 토큰을 생성하고 쿠키로 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Environment environment;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        String targetUrl = determineTargetUrl(request, response, authentication);

        log.info("targetUrl: {}", targetUrl);
        
        try {
            // 정확한 registrationId 사용 (CustomOAuth2UserService와 일치)
            String provider = authToken.getAuthorizedClientRegistrationId();
            String providerId = extractProviderId(oAuth2User);
            
            log.info("OAuth2 로그인 성공 처리: provider={}, providerId={}", provider, providerId);
            log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());
            
            // 사용자 정보 조회
            log.info("DB에서 소셜 사용자 조회 시도: provider={}, providerId={}", provider, providerId);
            Optional<SocialUser> socialUserOptional = socialUserRepository.findByProviderAndProviderId(provider, providerId);
            
            if (socialUserOptional.isEmpty()) {
                log.error("OAuth2 로그인 성공 후 사용자를 찾을 수 없습니다: provider={}, providerId={}", provider, providerId);
                log.error("DB 조회 조건: provider='{}', providerId='{}'", provider, providerId);
                // 모든 소셜 사용자 확인 (디버깅용)
                socialUserRepository.findAll().forEach(su -> 
                    log.error("DB에 있는 소셜 사용자: provider='{}', providerId='{}'", su.getProvider(), su.getProviderId())
                );
                getRedirectStrategy().sendRedirect(request, response, "/login?error=user_not_found");
                return;
            }
            
            SocialUser socialUser = socialUserOptional.get();
            Optional<User> userOptional = userRepository.findByUuid(socialUser.getUuid());
            
            if (userOptional.isEmpty()) {
                log.error("연결된 사용자를 찾을 수 없습니다: uuid={}", socialUser.getUuid());
                getRedirectStrategy().sendRedirect(request, response, "/login?error=user_not_found");
                return;
            }
            
            User user = userOptional.get();
            
            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getGrade().name());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getUuid(), user.getGrade().name());
            
            // 리프레시 토큰 저장
            saveRefreshToken(user.getUuid(), refreshToken);
            
            // 로그인 이력 기록
            user.recordLoginAttempt(request.getRemoteAddr(), request.getHeader("User-Agent"), true);
            userRepository.save(user);
            
            log.info("OAuth2 로그인 성공 처리 완료: userId={}, uuid={}", user.getId(), user.getUuid());

            addCookieJwtToken(response, accessToken, refreshToken);

            // 백엔드에서 리디렉션할 때
            String userData = URLEncoder.encode(
                "{\"nickname\":\"" + user.getNickname() + "\"," +
                "\"status\":\"" + user.getStatus() + "\"," +
                "\"grade\":\"" + user.getGrade() + "\"," +
                "\"gradeDesc\":\"" + UserGrade.toDesc(user.getGrade()) + "\"," +
                "\"profileImageUrl\":\"" + user.getProfileImageUrl() + "\"," +
                "\"lastLoginAt\":\"" + user.getUpdatedAt() + "\"}", 
                StandardCharsets.UTF_8
            );
            

            // 직접 HTML 응답으로 토큰 전달
            getRedirectStrategy().sendRedirect(request, response, targetUrl + "?user=" + userData);
            
        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 처리 중 오류 발생", e);
            getRedirectStrategy().sendRedirect(request, response, targetUrl + "/login?error=processing_failed");
        }
    }

    private void addCookieJwtToken(HttpServletResponse response, String accessToken, String refreshToken) {
        // 쿠키 생성
        ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(
                JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
                (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true);

        ResponseCookie refreshTokenCookie = CookieUtil.createResponseCookie(
                JwtTokenProvider.REFRESH_TOKEN_NAME, refreshToken,
                (int) (jwtTokenProvider.getJwtProperties().getRefreshTokenValidityInMs() / 1000), true, true);

        CookieUtil.addCookie(response, accessTokenCookie);
        CookieUtil.addCookie(response, refreshTokenCookie);
    }

    /**
     * Provider ID 추출
     */
    private String extractProviderId(OAuth2User oAuth2User) {
        // 네이버의 경우
        if (oAuth2User.getAttributes().containsKey("response")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            return (String) response.get("id");
        }
        
        return null;
    }

    /**
     * 리프레시 토큰 저장
     */
    private void saveRefreshToken(String uuid, String refreshToken) {
        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.revokeAllTokensByUuid(uuid);
        
        // 새로운 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .uuid(uuid)
                .token(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(jwtTokenProvider.getRefreshTokenExpireTimeAsLocalDateTime())
                .build();
        
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 현재 활성화된 프로파일 확인
        String[] activeProfiles = environment.getActiveProfiles();
        
        // 프로파일에 따른 기본 URL 설정
        String targetUrl = getDefaultTargetUrlByProfile(activeProfiles);
        
        return targetUrl;
    }
    
    /**
     * 프로파일에 따른 기본 타겟 URL 반환
     */
    private String getDefaultTargetUrlByProfile(String[] activeProfiles) {
        for (String profile : activeProfiles) {
            switch (profile.toLowerCase()) {
                case "local":
                    return "http://localhost:3000/login/success";
                case "prod":
                    return "https://pocamarket.co.kr/login/success";
                default:
                    // 기본값
                    return "https://pocamarket.co.kr/login/success";
            }
        }
        return "http://localhost:3000/login/success";
    }
} 