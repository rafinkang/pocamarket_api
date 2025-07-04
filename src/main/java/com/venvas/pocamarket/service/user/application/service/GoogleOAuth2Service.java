package com.venvas.pocamarket.service.user.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginResponse;
import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.entity.UserLoginHistory;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
import com.venvas.pocamarket.service.user.domain.repository.SocialUserRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Google OAuth2 로그인 서비스 구현체
 * Google OAuth2 API를 통한 소셜 로그인을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleOAuth2Service implements OAuth2UserService {
    
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${oauth2.google.client-id}")
    private String clientId;
    
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    
    @Value("${oauth2.google.token-uri}")
    private String tokenUri;
    
    @Value("${oauth2.google.user-info-uri}")
    private String userInfoUri;
    
    private static final String PROVIDER_TYPE = "google";
    
    @Override
    public String getProviderType() {
        return PROVIDER_TYPE;
    }
    
    @Override
    public String getAccessToken(String code, String redirectUri) {
        log.info("Google OAuth2 액세스 토큰 요청: code={}, redirectUri={}", code, redirectUri);
        
        try {
            log.info("Google OAuth2 액세스 토큰 요청 시작");
            log.info("clientId: {}", clientId);
            log.info("clientSecret: {}", clientSecret);
            log.info("code: {}", code);
            log.info("redirectUri: {}", redirectUri);
            log.info("tokenUri: {}", tokenUri);
            log.info("userInfoUri: {}", userInfoUri);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", redirectUri);
            
            log.info("Google OAuth2 액세스 토큰 요청 파라미터: {}", params);

            log.info("Google OAuth2 액세스 토큰 요청 헤더: {}", headers);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            log.info("Google OAuth2 액세스 토큰 요청 요청: {}", request);
            
            ResponseEntity<String> response = restTemplate.exchange(
                tokenUri, 
                HttpMethod.POST, 
                request, 
                String.class
            );
            log.info("Google OAuth2 액세스 토큰 요청 응답: {}", response);
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            
            log.info("Google OAuth2 액세스 토큰 획득 성공");
            return accessToken;
            
        } catch (Exception e) {
            log.error("Google OAuth2 액세스 토큰 획득 실패", e);
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "Google OAuth2 액세스 토큰 획득에 실패했습니다.");
        }
    }
    
    @Override
    public OAuth2UserInfoDto getUserInfo(String accessToken) {
        log.info("Google OAuth2 사용자 정보 요청");
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri, 
                HttpMethod.GET, 
                request, 
                String.class
            );
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            OAuth2UserInfoDto userInfo = OAuth2UserInfoDto.builder()
                    .provider(PROVIDER_TYPE)
                    .providerId(jsonNode.get("id").asText())
                    .email(jsonNode.has("email") ? jsonNode.get("email").asText() : null)
                    .name(jsonNode.has("name") ? jsonNode.get("name").asText() : null)
                    .nickname(jsonNode.has("given_name") ? jsonNode.get("given_name").asText() : null)
                    .profileImageUrl(jsonNode.has("picture") ? jsonNode.get("picture").asText() : null)
                    .emailVerified(jsonNode.has("verified_email") ? jsonNode.get("verified_email").asBoolean() : false)
                    .rawData(response.getBody())
                    .build();
            
            log.info("Google OAuth2 사용자 정보 획득 성공: providerId={}, email={}", userInfo.getProviderId(), userInfo.getEmail());
            return userInfo;
            
        } catch (Exception e) {
            log.error("Google OAuth2 사용자 정보 획득 실패", e);
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "Google OAuth2 사용자 정보 획득에 실패했습니다.");
        }
    }
    
    @Override
    @Transactional
    public SocialLoginResponse processSocialLogin(OAuth2UserInfoDto userInfo, String ipAddress, String userAgent) {
        log.info("Google 소셜 로그인 처리 시작: providerId={}, email={}", userInfo.getProviderId(), userInfo.getEmail());
        
        // 기존 소셜 사용자 확인
        Optional<SocialUser> existingSocialUser = socialUserRepository
                .findActiveByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());
        
        if (existingSocialUser.isPresent()) {
            // 기존 소셜 사용자 로그인 처리
            SocialUser socialUser = existingSocialUser.get();
            log.info("기존 소셜 사용자 로그인 처리: uuid={}", socialUser.getUuid());
            
            // 소셜 로그인 정보 업데이트
            socialUser.updateFromOAuth2UserInfo(userInfo);
            socialUserRepository.save(socialUser);
            
            // 연결된 사용자 정보 조회
            User user = userRepository.findByUuid(socialUser.getUuid())
                    .orElseThrow(() -> new IllegalStateException("연결된 사용자를 찾을 수 없습니다."));
            
            // 로그인 기록 저장
            user.recordLoginAttempt(
                    ipAddress,
                    userAgent,
                    true,
                    "소셜 로그인 성공"
            );
            userRepository.save(user);
            
            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getGrade().name());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getUuid(), user.getGrade().name());
            
            // 리프레시 토큰 저장
            saveRefreshToken(user.getUuid(), refreshToken);
            
            log.info("기존 소셜 사용자 로그인 처리 완료: uuid={}", user.getUuid());
            
            // 쿠키 생성
            ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(
                JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
                (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true
            );
            
            ResponseCookie refreshTokenCookie = CookieUtil.createResponseCookie(
                JwtTokenProvider.REFRESH_TOKEN_NAME, refreshToken,
                (int) (jwtTokenProvider.getJwtProperties().getRefreshTokenValidityInMs() / 1000), true, true
            );
            
            return SocialLoginResponse.from(user, socialUser, accessToken, refreshToken, 
                                           accessTokenCookie, refreshTokenCookie, false);
        }
        
        // 기존 사용자 확인 (이메일 기반)
        if (userInfo.getEmail() != null) {
            Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());
            
            if (existingUser.isPresent()) {
                // 기존 사용자에 소셜 로그인 연결
                User user = existingUser.get();
                log.info("기존 사용자에 소셜 로그인 연결: uuid={}", user.getUuid());
                
                // 새 소셜 로그인 정보 생성
                SocialUser newSocialUser = SocialUser.createFromOAuth2UserInfo(user.getUuid(), userInfo);
                log.info("새 소셜 사용자 생성: uuid={}", user.getUuid());
                socialUserRepository.save(newSocialUser);
                
                // 로그인 기록 저장
                user.recordLoginAttempt(
                        ipAddress,
                        userAgent,
                        true,
                        "소셜 로그인 연결 성공"
                );
                userRepository.save(user);
                
                // JWT 토큰 생성
                String accessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getGrade().name());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getUuid(), user.getGrade().name());
                
                // 리프레시 토큰 저장
                saveRefreshToken(user.getUuid(), refreshToken);
                
                log.info("새 소셜 사용자 등록 처리 완료: uuid={}", user.getUuid());
                
                // 쿠키 생성
                ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
                    (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true
                );
                
                ResponseCookie refreshTokenCookie = CookieUtil.createResponseCookie(
                    JwtTokenProvider.REFRESH_TOKEN_NAME, refreshToken,
                    (int) (jwtTokenProvider.getJwtProperties().getRefreshTokenValidityInMs() / 1000), true, true
                );
                
                return SocialLoginResponse.from(user, newSocialUser, accessToken, refreshToken, 
                                               accessTokenCookie, refreshTokenCookie, false);
            }
        }
        
        // 새 사용자 등록
        String uuid = UUID.randomUUID().toString();
        
        // 닉네임 생성 (이름이 있으면 이름, 없으면 이메일 앞부분 사용)
        String nickname = userInfo.getName();
        if (nickname == null || nickname.isEmpty()) {
            nickname = userInfo.getEmail() != null ? userInfo.getEmail().split("@")[0] : "사용자" + System.currentTimeMillis();
        }
        
        // 새 사용자 생성
        User newUser = User.builder()
                .uuid(uuid)
                .loginId(userInfo.getProvider() + "_" + userInfo.getProviderId())
                .password("") // 소셜 로그인 사용자는 비밀번호 없음
                .name(userInfo.getName() != null ? userInfo.getName() : nickname)
                .nickname(nickname)
                .email(userInfo.getEmail())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .emailVerified(userInfo.isEmailVerified())
                .gradeCode(UserGrade.LV01.getCode())
                .build();
        
        userRepository.save(newUser);
        
        // 새 소셜 로그인 정보 생성
        SocialUser newSocialUser = SocialUser.createFromOAuth2UserInfo(uuid, userInfo);
        socialUserRepository.save(newSocialUser);
        
        // 로그인 기록 저장
        newUser.recordLoginAttempt(
                ipAddress,
                userAgent,
                true,
                "소셜 로그인 회원가입 성공"
        );
        userRepository.save(newUser);
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(newUser.getUuid(), newUser.getGrade().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(newUser.getUuid(), newUser.getGrade().name());
        
        // 리프레시 토큰 저장
        saveRefreshToken(newUser.getUuid(), refreshToken);
        
        // 쿠키 생성
        ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(
            JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
            (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true
        );
        
        ResponseCookie refreshTokenCookie = CookieUtil.createResponseCookie(
            JwtTokenProvider.REFRESH_TOKEN_NAME, refreshToken,
            (int) (jwtTokenProvider.getJwtProperties().getRefreshTokenValidityInMs() / 1000), true, true
        );
        
        log.info("새 소셜 사용자 회원가입 처리 완료: uuid={}", newUser.getUuid());
        
        return SocialLoginResponse.from(newUser, newSocialUser, accessToken, refreshToken, 
                                       accessTokenCookie, refreshTokenCookie, true);
    }
    
    /**
     * 리프레시 토큰 저장
     */
    private void saveRefreshToken(String uuid, String refreshToken) {
        // 기존 리프레시 토큰 만료 처리
        refreshTokenRepository.revokeAllTokensByUuid(uuid);
        
        // 새 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .uuid(uuid)
                .token(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(jwtTokenProvider.getRefreshTokenExpireTimeAsLocalDateTime())
                .build();
        
        refreshTokenRepository.save(refreshTokenEntity);
    }
} 