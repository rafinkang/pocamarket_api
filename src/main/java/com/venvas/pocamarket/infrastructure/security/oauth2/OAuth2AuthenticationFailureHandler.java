package com.venvas.pocamarket.infrastructure.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 실패 처리 핸들러
 * 로그인 실패 시 적절한 에러 페이지로 리다이렉트
 */
@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
                                       AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = exception.getLocalizedMessage();
        log.error("OAuth2 로그인 실패: {}", errorMessage, exception);
        
        // 로그인 실패 시 로그인 페이지로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/login?error=oauth2_failed");
    }
} 