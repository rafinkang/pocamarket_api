package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginRequest;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginResponse;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 소셜 로그인 통합 관리 서비스
 * 여러 OAuth2 제공자를 관리하고 적절한 구현체를 선택하여 소셜 로그인을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialLoginService {
    
    private final List<OAuth2UserService> oAuth2UserServices;
    private Map<String, OAuth2UserService> providerServiceMap;
    
    /**
     * 서비스 초기화 시 제공자별 서비스 맵 생성
     */
    @jakarta.annotation.PostConstruct
    public void initProviderMap() {
        providerServiceMap = oAuth2UserServices.stream()
                .collect(Collectors.toMap(
                    OAuth2UserService::getProviderType,
                    Function.identity()
                ));
        
        log.info("소셜 로그인 제공자 초기화 완료: {}", providerServiceMap.keySet());
    }
    
    /**
     * 소셜 로그인 처리
     * 
     * @param request 소셜 로그인 요청 정보
     * @return 소셜 로그인 응답 정보
     */
    @Transactional
    public SocialLoginResponse processSocialLogin(SocialLoginRequest request) {
        log.info("소셜 로그인 처리 시작: provider={}, redirectUri={}", request.getProvider(), request.getRedirectUri());
        
        // 1. 제공자별 서비스 조회
        OAuth2UserService oAuth2Service = getOAuth2Service(request.getProvider());
        
        try {
            // 2. 액세스 토큰 획득
            String accessToken = oAuth2Service.getAccessToken(request.getCode(), request.getRedirectUri());
            
            // 3. 사용자 정보 획득
            OAuth2UserInfoDto userInfo = oAuth2Service.getUserInfo(accessToken);
            
            // 4. 소셜 로그인 처리 (사용자 생성 또는 로그인)
            SocialLoginResponse response = oAuth2Service.processSocialLogin(
                userInfo, request.getIpAddress(), request.getUserAgent()
            );
            
            log.info("소셜 로그인 처리 완료: provider={}, uuid={}, isNewUser={}",
                    response.getProvider(), response.getUuid(), response.isNewUser());
            
            return response;
            
        } catch (Exception e) {
            log.error("소셜 로그인 처리 실패: provider={}, error={}", request.getProvider(), e.getMessage(), e);
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "소셜 로그인 처리에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 제공자별 OAuth2 서비스 조회
     * 
     * @param provider OAuth2 제공자 타입
     * @return OAuth2 사용자 서비스
     */
    private OAuth2UserService getOAuth2Service(String provider) {
        OAuth2UserService service = providerServiceMap.get(provider.toLowerCase());
        
        if (service == null) {
            log.error("지원하지 않는 OAuth2 제공자: {}", provider);
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
        
        return service;
    }
    
    /**
     * 지원하는 OAuth2 제공자 목록 조회
     * 
     * @return 지원하는 제공자 목록
     */
    public List<String> getSupportedProviders() {
        return List.copyOf(providerServiceMap.keySet());
    }
    
    /**
     * 특정 제공자 지원 여부 확인
     * 
     * @param provider OAuth2 제공자 타입
     * @return 지원 여부
     */
    public boolean isProviderSupported(String provider) {
        return providerServiceMap.containsKey(provider.toLowerCase());
    }
} 