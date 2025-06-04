package com.venvas.pocamarket.infrastructure.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    // 쿠키 생성
    public static ResponseCookie createResponseCookie(String name, String value, int maxAge, boolean httpOnly, boolean secure) {
        return ResponseCookie.from(name, value)
                .path("/")
                .maxAge(maxAge) // 토큰 만료 시간
                .sameSite("Lax") // CSRF 보호
                .httpOnly(httpOnly) // HTTPOnly 설정 - XSS 공격 방지, 자바스크립트 접근 불가
                .secure(secure) // HTTPS 사용 시 설정 (로컬개발시 false)
                .build();
    }

    // 쿠키 조회
    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = createResponseCookie(name, null, 0, true, true);
        addCookie(response, cookie);
    }

    // 쿠키 등록
    public static void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader("Set-Cookie", cookie.toString());
    }
}