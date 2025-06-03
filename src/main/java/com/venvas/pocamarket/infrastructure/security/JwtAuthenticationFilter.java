package com.venvas.pocamarket.infrastructure.security;

import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider; // 토큰 검증/파싱 유틸
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 유저 정보 추출
            String uuid = jwtTokenProvider.getUuid(token);

            // 여기서 db에서 유저 정보 새로 받아올지 고민
            UserDetails user = userRepository.findByUuid(uuid)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "찾는 UUID가 없습니다."));

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.info("토큰 인증 실패");
        }

        filterChain.doFilter(request, response);
    }
}
