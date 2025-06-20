package com.venvas.pocamarket.infrastructure.security;

import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.exception.JwtCustomException;
import com.venvas.pocamarket.service.user.domain.exception.JwtErrorCode;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider; // 토큰 검증/파싱 유틸
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties = new JwtProperties();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT 필터 실행 - URI: {}", request.getRequestURI());

        String accessToken = jwtTokenProvider.resolveToken(request, JwtTokenProvider.ACCESS_TOKEN_NAME);
        String refreshToken = jwtTokenProvider.resolveToken(request, JwtTokenProvider.REFRESH_TOKEN_NAME);

        JwtErrorCode accessTokenErrorCode = jwtTokenProvider.validateToken(accessToken);
        JwtErrorCode refreshTokenErrorCode = jwtTokenProvider.validateToken(refreshToken);

        log.info("Filter 접속 accessToken = {}", accessToken);
        log.info("accessTokenErrorCode = {}", accessTokenErrorCode);
        log.info("refreshTokenErrorCode = {}", refreshTokenErrorCode);

        try {
            if (accessToken != null && accessTokenErrorCode == null) {
                // 토큰에서 유저 정보 추출
                String uuid = jwtTokenProvider.getUuid(accessToken);
                UserGrade grade = UserGrade.valueOf(jwtTokenProvider.getGrade(accessToken));

                if (uuid == null) {
                    throw new JwtCustomException(JwtErrorCode.INVALID_INFO);
                }

                saveSecurityContextHolder(uuid, grade);
            } else if (refreshToken != null && refreshTokenErrorCode == null) {
                // 리프레시 토큰이 유효한 경우, 새로운 액세스 토큰 발급 로직 추가 가능
                // refresh_token 테이블에 uuid 기준으로 조회
                refreshTokenRepository.findValidTokensByUuid(jwtTokenProvider.getUuid(refreshToken), LocalDateTime.now())
                        .ifPresentOrElse(
                                token -> {
                                    // 유효한 리프레시 토큰이 있는 경우
                                    log.info("유효한 리프레시 토큰 발견: {}", token);

                                    // DB에서 유저 정보 조회 후 AccessToken 재발급 로직 추가
                                    User user = userRepository.findByUuid(jwtTokenProvider.getUuid(refreshToken))
                                            .orElse(null);

                                    if (user == null) {
                                        throw new JwtCustomException(JwtErrorCode.INVALID_INFO);
                                    }

                                    createTokenAddCookie(response, user);

                                    saveSecurityContextHolder(user.getUuid(), user.getGrade());
                                },
                                () -> {
                                    // 유효한 리프레시 토큰이 없는 경우 로그아웃 처리
                                    log.info("유효한 리프레시 토큰 없음, 로그아웃 처리");
                                    CookieUtil.deleteCookie(response, JwtTokenProvider.ACCESS_TOKEN_NAME);
                                    CookieUtil.deleteCookie(response, JwtTokenProvider.REFRESH_TOKEN_NAME);
                                    throw new JwtCustomException(JwtErrorCode.TOKEN_EXPIRED);
                                });
            } else {
                // 토큰이 없거나 유효하지 않은 경우 - 인증이 필요한 경로에서만 예외 발생
                log.info("토큰 없음 또는 유효하지 않음 - URI: {}", request.getRequestURI());
                // SecurityConfig의 authorizeHttpRequests에서 처리하도록 넘김
            }
        } catch (JwtException e) {
            log.info("JWT 처리 중 에러 발생: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    // Access 토큰 생성 후, 쿠키 등록
    private void createTokenAddCookie(HttpServletResponse response, User user) {
        // 새로운 Access 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getGrade().name());

        // 토큰 값을 넣은 쿠키 생성 후, 등록
        ResponseCookie accessCookie = CookieUtil.createResponseCookie(JwtTokenProvider.ACCESS_TOKEN_NAME,
                newAccessToken, (int) (jwtProperties.getAccessTokenValidityInMs() / 1000), true, true);
        CookieUtil.addCookie(response, accessCookie);
    }

    // 인증 유저 정보 전달
    private void saveSecurityContextHolder(String uuid, UserGrade grade) {
        // 엑세스 토큰 정보 가져오기
        UserDetails user = new UserDetailDto(uuid, grade);

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());

        // SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
