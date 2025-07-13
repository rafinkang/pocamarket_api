package com.venvas.pocamarket.infrastructure.security.oauth2;

import com.venvas.pocamarket.infrastructure.security.oauth2.user.OAuth2UserInfo;
import com.venvas.pocamarket.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.SocialUserRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * OAuth2 사용자 정보 처리 서비스
 * Spring Security OAuth2 Client의 사용자 정보 로드 및 처리를 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("OAuth2 사용자 처리 중 오류 발생", e);
            throw new OAuth2AuthenticationException(e.getMessage());
        }
    }

    /**
     * OAuth2 사용자 정보 처리
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // OAuth2 제공자 식별 (google, naver, kakao 등)
        // application.yml의 spring.security.oauth2.client.registration.{registrationId} 에서 설정한 값
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // OAuth2 제공자별 사용자 고유 식별자가 담긴 필드명
        // 예: Google의 경우 "sub", Naver의 경우 "response" 등
        // 각 제공자마다 사용자 정보 JSON 구조가 다르므로 어떤 필드가 사용자 식별자인지 지정
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        log.info("OAuth2 사용자 정보 처리 시작: provider={}, userNameAttributeName={}", 
                registrationId, userNameAttributeName);

        // OAuth2 사용자 정보 추출
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oAuth2User.getAttributes());

        log.info("OAuth2 사용자 정보 추출 완료: provider={}, providerId={}, email={}, name={}", 
                oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId(), 
                oAuth2UserInfo.getEmail(), oAuth2UserInfo.getName());

        if (oAuth2UserInfo.getProviderId() == null || oAuth2UserInfo.getProviderId().isEmpty()) {
            log.error("OAuth2 제공자에서 사용자 ID를 찾을 수 없습니다: provider={}", registrationId);
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "OAuth2 제공자에서 사용자 ID를 찾을 수 없습니다");
        }

        // 기존 소셜 사용자 확인
        log.info("기존 소셜 사용자 검색: provider={}, providerId={}", registrationId, oAuth2UserInfo.getProviderId());
        Optional<SocialUser> existingSocialUser = socialUserRepository.findActiveByProviderAndProviderId(
                registrationId, oAuth2UserInfo.getProviderId());
        
        log.info("기존 소셜 사용자 검색 결과: {}", existingSocialUser.isPresent() ? "발견" : "없음");

        User user;
        if (existingSocialUser.isPresent()) {
            // 기존 사용자 업데이트
            SocialUser socialUser = existingSocialUser.get();
            user = updateExistingUser(socialUser, oAuth2UserInfo);
        } else {
            // 새 사용자 생성
            user = createNewUser(registrationId, oAuth2UserInfo);
        }

        // 사용자 정보를 포함한 OAuth2User 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                userNameAttributeName
        );
    }

    /**
     * 기존 사용자 정보 업데이트
     */
    private User updateExistingUser(SocialUser socialUser, OAuth2UserInfo oAuth2UserInfo) {
        log.info("기존 OAuth2 사용자 정보 업데이트: uuid={}, provider={}", 
                socialUser.getUuid(), socialUser.getProvider());

        // 소셜 사용자 정보 업데이트
        socialUser.updateFromOAuth2UserInfo(createOAuth2UserInfoDto(oAuth2UserInfo));
        socialUser = socialUserRepository.save(socialUser);
        
        log.info("기존 소셜 사용자 업데이트 완료: provider={}, providerId={}, uuid={}", 
                socialUser.getProvider(), socialUser.getProviderId(), socialUser.getUuid());

        // 사용자 정보 조회
        Optional<User> userOptional = userRepository.findStatusByUuid(socialUser.getUuid());
        if (userOptional.isEmpty()) {
            log.error("연결된 사용자를 찾을 수 없습니다: uuid={}", socialUser.getUuid());
            throw new UserException(UserErrorCode.UNKNOWN_ERROR, "연결된 사용자를 찾을 수 없습니다");
        }

        User user = userOptional.get();
        
        // 사용자 정보 업데이트
        if (oAuth2UserInfo.getEmail() != null) {
            user.setEmail(oAuth2UserInfo.getEmail());
        }
        if (oAuth2UserInfo.getProfileImageUrl() != null) {
            user.setProfileImageUrl(oAuth2UserInfo.getProfileImageUrl());
        }
        
        return userRepository.save(user);
    }

    /**
     * 새 사용자 생성
     */
    private User createNewUser(String provider, OAuth2UserInfo oAuth2UserInfo) {
        log.info("새 OAuth2 사용자 생성: provider={}, providerId={}", 
                provider, oAuth2UserInfo.getProviderId());

        // 새 사용자 생성
        User user = User.builder()
                .uuid(UUID.randomUUID().toString())
                .loginId(generateUniqueLoginId(provider))
                .password("") // 소셜 로그인 사용자는 비밀번호 불필요
                .name(oAuth2UserInfo.getName()) // getName -> nickname값 반환
                .nickname(generateUniqueNickname(oAuth2UserInfo.getNickname()))
                .email(oAuth2UserInfo.getEmail())
                .emailVerified(true) // 소셜 로그인 사용자는 이메일 인증 완료로 간주
                .profileImageUrl(oAuth2UserInfo.getProfileImageUrl())
                .build();

        // 상태 및 등급 설정
        user.setStatus(UserStatus.ACTIVE);
        user.setGrade(UserGrade.LV01);

        user = userRepository.save(user);

        // 소셜 사용자 정보 생성
        SocialUser socialUser = SocialUser.createFromOAuth2UserInfo(user.getUuid(), createOAuth2UserInfoDto(oAuth2UserInfo));
        socialUser = socialUserRepository.save(socialUser);

        log.info("새 소셜 사용자 생성 완료: provider={}, providerId={}, uuid={}", 
                socialUser.getProvider(), socialUser.getProviderId(), socialUser.getUuid());

        return user;
    }

    /**
     * OAuth2UserInfo를 OAuth2UserInfoDto로 변환
     */
    private com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto createOAuth2UserInfoDto(OAuth2UserInfo oAuth2UserInfo) {
        return com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto.builder()
                .provider(oAuth2UserInfo.getProvider())
                .providerId(oAuth2UserInfo.getProviderId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .nickname(oAuth2UserInfo.getNickname())
                .profileImageUrl(oAuth2UserInfo.getProfileImageUrl())
                .emailVerified(true)
                .build();
    }

    /**
     * 유니크한 로그인 ID 생성
     */
    private String generateUniqueLoginId(String provider) {
        String baseLoginId = provider + "_" + System.currentTimeMillis();
        String loginId = baseLoginId;
        int counter = 1;
        
        while (userRepository.existsByLoginId(loginId)) {
            loginId = baseLoginId + "_" + counter++;
        }
        
        return loginId;
    }

    /**
     * 유니크한 닉네임 생성
     */
    public String generateUniqueNickname(String originalNickname) {
        if (originalNickname == null || originalNickname.trim().isEmpty()) {
            originalNickname = "사용자";
        }
        
        String baseNickname = originalNickname.trim();
        String nickname = baseNickname;
        int counter = 1;
        
        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + "_" + counter++;
        }
        
        return nickname;
    }
} 