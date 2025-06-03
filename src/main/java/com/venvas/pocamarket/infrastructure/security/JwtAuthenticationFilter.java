package com.venvas.pocamarket.infrastructure.security;

import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider; // 토큰 검증/파싱 유틸
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveToken(request, "accessToken");
        String refreshToken = jwtTokenProvider.resolveToken(request, "refreshToken");

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            // 토큰에서 유저 정보 추출
            String uuid = jwtTokenProvider.getUuid(accessToken);
            UserGrade grade = UserGrade.valueOf(jwtTokenProvider.getGrade(accessToken));

            // 엑세스 토큰 정보 가져오기
            UserDetails user = new UserDetailDto(uuid, grade);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            // 리프레시 토큰이 유효한 경우, 새로운 액세스 토큰 발급 로직 추가 가능
            // refresh_token 테이블에 uuid 기준으로 조회
            refreshTokenRepository.findValidTokensByUuid(jwtTokenProvider.getUuid(refreshToken),
                    new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .ifPresentOrElse(
                    token -> {
                        // 유효한 리프레시 토큰이 있는 경우
                        log.info("유효한 리프레시 토큰 발견: {}", token);
                        // DB에서 유저 정보 조회 후 AccessToken 재발급 로직 추가
                        User user = userRepository.findByUuid(jwtTokenProvider.getUuid(refreshToken))
                                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
                        
                        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getGrade().name());
                        CookieUtil.addCookie(response, "accessToken", newAccessToken, (int)(jwtProperties.getAccessTokenValidityInMs() / 1000), true, true);
                    },
                    () -> {
                        // 유효한 리프레시 토큰이 없는 경우 로그아웃 처리
                        log.info("유효한 리프레시 토큰 없음, 로그아웃 처리");
                        CookieUtil.deleteCookie(response, "accessToken");
                        CookieUtil.deleteCookie(response, "refreshToken");
                    }
                );

            // 살아있으면 = DB에서 유저 정보 조회 후 AccessToken 재발급
            
            // 아니면 = 로그아웃처리: AccessToken, RefreshToken 쿠키에서 모두 삭제
        } else {
            log.info("토큰 인증 실패");
        }

        filterChain.doFilter(request, response);
    }
}
