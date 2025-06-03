package com.venvas.pocamarket.infrastructure.util;


import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import com.venvas.pocamarket.service.user.application.dto.JwtToken;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final Date accessTokenExpireTime;
    private final Date refreshTokenExpireTime;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        this.accessTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenValidityInMs());
        this.refreshTokenExpireTime = new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenValidityInMs());
    }

    // 1. 토큰 생성
    public JwtToken createToken(User user) {
        String accessToken = createAccessToken(user.getUuid(), user.getNickname(), user.getGrade().name());
        String refreshToken = createRefreshToken(user.getUuid(), user.getNickname());

        return JwtToken.builder()
                .grantType("Bearer") // HTTP 요청의 Authorization 헤더에 포함해서 전송
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 2. uuid 추출
    public String getUuid(String token) {
        log.info("토큰 파싱 바디 값 : {}", getAllClaimsFromToken(token).toString());
        return getAllClaimsFromToken(token).get("uuid", String.class);
    }

    public String getNickname(String token) {
        return getAllClaimsFromToken(token).get("nickname", String.class);
    }

    public String getGrade(String token) {
        return getAllClaimsFromToken(token).get("grade", String.class);
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 4. Request에서 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String createAccessToken(String uuId, String nickname, String grade) {
        return Jwts.builder() // JWT 토큰 빌더 생성
//            .setSubject(user.getId().toString()) // 토큰 제목(subject)으로 사용자 ID 설정
            .setSubject("AccessToken") // 토큰 제목(subject)으로 사용자 ID 설정
//            .claim("auth", authorities)
            .claim("uuid", uuId) // 사용자 uuid를 클레임으로 추가
            .claim("nickname", nickname) // 사용자 닉네임을 클레임으로 추가
            .claim("grade", grade) // 사용자 등급을 클레임으로 추가
            .setIssuedAt(new Date()) // 토큰 발급 시간 설정
            .setExpiration(accessTokenExpireTime) // 토큰 만료 시간 설정
            .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 시크릿 키로 서명
            .compact(); // 최종적으로 토큰 문자열로 변환
    }

    private String createRefreshToken(String uuId, String nickname) {
        return Jwts.builder()
//                .setHeaderParams(Map.of("typ", "JWT"))
                .setSubject("RefreshToken")
                .claim("uuid", uuId) // 사용자 uuid를 클레임으로 추가
                .claim("nickname", nickname) // 사용자 닉네임을 클레임으로 추가
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.INACTIVE) {
            log.warn("비활성화된 계정: loginId={}", user.getLoginId());
            throw new UserException(UserErrorCode.ACCOUNT_LOCKED);
        } else if (user.getStatus() == UserStatus.SUSPENDED) {
            log.warn("일시 정지된 계정: loginId={}", user.getLoginId());
            throw new UserException(UserErrorCode.ACCOUNT_LOCKED);
        } else if (user.getStatus() == UserStatus.DELETED) {
            log.warn("삭제된 계정: loginId={}", user.getLoginId());
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
