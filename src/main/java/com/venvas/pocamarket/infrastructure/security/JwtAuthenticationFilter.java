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

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);
        log.info("========================================================= request = {}", request.getHeader("Authorization"));
        log.info("========================================================= token = {}", token);
        // 2. 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token) == null) {
            // 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가져와 SecurityContext에 저장
            String uuid = jwtTokenProvider.getUuid(token);
            UserGrade grade = UserGrade.valueOf(jwtTokenProvider.getGrade(token));

            if (uuid == null) {
                throw new JwtCustomException(JwtErrorCode.INVALID_INFO);
            }
            saveSecurityContextHolder(uuid, grade);
        }


        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보를 꺼내오는 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
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
