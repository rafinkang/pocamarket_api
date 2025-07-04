package com.venvas.pocamarket.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.service.user.domain.exception.JwtCustomException;
import com.venvas.pocamarket.service.user.domain.exception.JwtErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 실패 처리
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.info("JwtAuthenticationEntryPoint 실행 인증 권한 실패 401 처리 부분, requestURI: {}", request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = new HashMap<>();

        Object exception = request.getAttribute("exception");
        if (exception instanceof JwtCustomException ex) {
            body.put("errorMessage", ex.getMessage());
            body.put("code", ex.getErrorCode().getCode());
        } else if (exception == null) {
            body.put("error", "인증 권한 에러");
            body.put("code", JwtErrorCode.FAIL_AUTHENTICATION);
        }

        response.getWriter().write(objectMapper.writeValueAsString(body));
        // ResponseEntity.ok(ApiResponse.error("Not Authenticated Request",
        // JwtErrorCode.FAIL_AUTHENTICATION.getCode()));
        // response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
    }
}
