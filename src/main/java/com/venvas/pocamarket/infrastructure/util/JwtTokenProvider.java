package com.venvas.pocamarket.infrastructure.util;


import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import com.venvas.pocamarket.service.user.domain.exception.JwtErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";

    private final Key key;

    @Getter
    private final JwtProperties jwtProperties;
    @Getter
    private Date accessTokenExpireTime;
    @Getter
    private Date refreshTokenExpireTime;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        this.accessTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenValidityInMs());
        this.refreshTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenValidityInMs());
    }

    public String createAccessToken(String uuid, String grade) {
        this.accessTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenValidityInMs());

        return Jwts.builder() // JWT 토큰 빌더 생성
            .setSubject(ACCESS_TOKEN_NAME) // 토큰 제목(subject)으로 사용자 ID 설정
            .claim("uuid", uuid) // 사용자 uuid를 클레임으로 추가
            .claim("grade", grade) // 사용자 등급을 클레임으로 추가
            .setIssuedAt(new Date()) // 토큰 발급 시간 설정
            .setExpiration(accessTokenExpireTime) // 토큰 만료 시간 설정
            .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 시크릿 키로 서명
            .compact(); // 최종적으로 토큰 문자열로 변환
    }

    public String createRefreshToken(String uuid) {
        this.refreshTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenValidityInMs());
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_NAME)
                .claim("uuid", uuid) // 사용자 uuid를 클레임으로 추가
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUuid(String token) {
        return getAllClaimsFromToken(token).get("uuid", String.class);
    }

    public String getGrade(String token) {
        return getAllClaimsFromToken(token).get("grade", String.class);
    }

    // 3. 토큰 유효성 검증
    public JwtErrorCode validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            if(claims.getBody().getExpiration().before(new Date())) {
                return JwtErrorCode.TOKEN_EXPIRED;
            }
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            return JwtErrorCode.FAIL_AUTHENTICATION;
        }
    }

    // 4. Request에서 토큰 추출
    public String resolveToken(HttpServletRequest req, String tokenType) {
        return CookieUtil.getCookieValue(req, tokenType);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
