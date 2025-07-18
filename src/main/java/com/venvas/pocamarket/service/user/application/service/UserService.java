package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.infrastructure.util.CookieUtil;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.application.dto.*;
import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
import com.venvas.pocamarket.service.user.domain.repository.SocialUserRepository;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 * 사용자 생성, 조회, 수정 등의 기능을 제공
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {
    
    private static final List<String> PROHIBITED_NICKNAME_WORDS = Arrays.asList(
            "admin", "root", "system", "manager", "superuser", "administrator", "관리자");

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialUserRepository socialUserRepository;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, 
            PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, SocialUserRepository socialUserRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.socialUserRepository = socialUserRepository;
    }

    /**
     * 새로운 사용자를 생성
     * 
     * @param request 사용자 생성 요청 DTO
     * @return 생성된 사용자 엔티티
     * @throws UserException 유효성 검증에 실패한 경우 (중복 ID, 이메일 등)
     */
    @Transactional
    public User register(UserCreateRequest request) {
        log.info("사용자 생성 시작: loginId={}", request.getLoginId());

        // 입력값 유효성 검증
        validateUserData(request);

        // 중복 체크
        validateDuplicateUser(request);

        // 사용자 엔티티 생성 및 저장
        User savedUser = createAndSaveUser(request);

        log.info("사용자 생성 완료: userId={}, loginId={}", savedUser.getId(), savedUser.getLoginId());
        return savedUser;
    }

    /**
     * 사용자 엔티티를 생성하고 저장합니다.
     * 
     * @param request 사용자 생성 요청 DTO
     * @return 저장된 사용자 엔티티
     */
    private User createAndSaveUser(UserCreateRequest request) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 엔티티 생성 및 저장
        User user = User.createFromRequest(request, encodedPassword);
        return userRepository.save(user);
    }

    /**
     * 사용자 생성 요청의 유효성을 검증
     * - 로그인 ID 중복 검증
     * - 이메일 중복 검증
     * 
     * @param request 사용자 생성 요청 DTO
     * @throws UserException 유효성 검증에 실패한 경우
     */
    /**
     * 사용자 데이터의 유효성을 검증합니다.
     * Bean Validation에 추가로 비즈니스 로직에 따른 검증을 수행합니다.
     * 
     * @param request 사용자 생성 요청 DTO
     * @throws UserException 유효성 검증 실패 시
     */
    private void validateUserData(UserCreateRequest request) {
        validateLoginIdFormat(request.getLoginId());
        validateNickname(request.getNickname());
        // 이메일은 선택 사항으로 처리
    }

    /**
     * 로그인 ID 형식을 검증합니다.
     * 공백, @를 포함하지 않아야 합니다.
     * 
     * @param loginId 검증할 로그인 ID
     * @throws UserException 형식에 맞지 않는 경우
     */
    private void validateLoginIdFormat(String loginId) {
        // 로그인 ID 형식 추가 검증 (공백, 특수문자 제한)
        if (loginId == null || loginId.contains(" ") || loginId.contains("@")) {
            log.warn("로그인 ID 형식 유효성 검증 실패: {}", loginId);
            throw new UserException(UserErrorCode.INVALID_LOGIN_ID_FORMAT);
        }
    }

    /**
     * 닉네임이 금지된 단어를 포함하는지 검사합니다.
     * 
     * @param nickname 검증할 닉네임
     * @throws UserException 금지된 단어를 포함하는 경우
     */
    private void validateNickname(String nickname) {
        String lowercaseNickname = nickname.toLowerCase();

        for (String prohibitedWord : PROHIBITED_NICKNAME_WORDS) {
            if (lowercaseNickname.contains(prohibitedWord)) {
                log.warn("금지된 닉네임 단어 포함: {}, 금지된 단어: {}", nickname, prohibitedWord);
                throw new UserException(UserErrorCode.INVALID_NICKNAME);
            }
        }
    }

    /**
     * 사용자 중복 여부를 검증합니다.
     * 
     * @param request 사용자 생성 요청 DTO
     * @throws UserException 중복된 사용자가 존재하는 경우
     */
    private void validateDuplicateUser(UserCreateRequest request) {
        // 로그인 ID 중복 검사
        validateDuplicateLoginId(request.getLoginId());

        // 이메일 중복 검사 (이메일이 있는 경우에만)
        String email = request.getEmail();
        if (hasEmailValue(email)) {
            validateDuplicateEmail(email);
        }
    }

    /**
     * 이메일 값이 유효한지 확인합니다.
     * 
     * @param email 검사할 이메일
     * @return 유효한 이메일이 있으면 true
     */
    private boolean hasEmailValue(String email) {
        return email != null && !email.trim().isEmpty();
    }

    // validateEmailRequired 메서드는 현재 이메일이 선택사항이므로 사용하지 않음

    /**
     * 로그인 ID 중복을 검사합니다.
     * 
     * @param loginId 검사할 로그인 ID
     * @throws UserException 중복된 로그인 ID가 존재하는 경우
     */
    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            log.warn("중복된 로그인 ID 검출: {}", loginId);
            throw new UserException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    /**
     * 이메일 중복을 검사합니다.
     * 
     * @param email 검사할 이메일
     * @throws UserException 중복된 이메일이 존재하는 경우
     */
    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("중복된 이메일 검출: {}", email);
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    /**
     * 사용자 로그인을 처리합니다.
     * 
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO
     * @throws UserException 로그인 실패 시 (사용자 없음, 비밀번호 틀림, 계정 잠금 등)
     */
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        log.info("로그인 시도: loginId={}", request.getLoginId());

        // 1. 사용자 조회
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 로그인 ID: {}", request.getLoginId());
                    return new UserException(UserErrorCode.USER_NOT_FOUND);
                });

        // 2. 계정 상태 확인
        validateUserStatus(user);

        // 3. 비밀번호 확인
        boolean isValidPassword = validatePassword(user, request.getPassword());

        // 4. 로그인 기록 남기기
        String failReason = null;
        if (!isValidPassword) {
            failReason = "비밀번호 불일치";
            user.recordLoginAttempt(request.getIpAddress(), request.getUserAgent(), false, failReason);
            userRepository.save(user);
            log.warn("비밀번호 불일치: loginId={}", request.getLoginId());
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        // 5. 로그인 성공 처리
        user.recordLoginAttempt(request.getIpAddress(), request.getUserAgent(), true);
        User updatedUser = userRepository.save(user);

        // 6. JWT 토큰 생성 (실제 구현에서는 JWT 서비스를 통해 토큰 생성)
        String accessToken = jwtTokenProvider.createAccessToken(updatedUser.getUuid(), updatedUser.getGrade().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(updatedUser.getUuid(), updatedUser.getGrade().name());

        // 7. 토큰 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .uuid(updatedUser.getUuid())
                .token(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(jwtTokenProvider.getRefreshTokenExpireTimeAsLocalDateTime())
                .build();

        // 오래된 리프레쉬 토큰 만료 처리 (선택적)
        refreshTokenRepository.revokeAllTokensByUuid(updatedUser.getUuid());
        refreshTokenRepository.save(refreshTokenEntity);

        // 8. 쿠키 생성
        ResponseCookie accessTokenCookie = CookieUtil.createResponseCookie(JwtTokenProvider.ACCESS_TOKEN_NAME, accessToken,
                (int) (jwtTokenProvider.getJwtProperties().getAccessTokenValidityInMs() / 1000), true, true);

        ResponseCookie refreshTokenCookie = CookieUtil.createResponseCookie(JwtTokenProvider.REFRESH_TOKEN_NAME, refreshToken,
                (int) (jwtTokenProvider.getJwtProperties().getRefreshTokenValidityInMs() / 1000), true, true);

        log.info("로그인 성공: userId={}, loginId={}", updatedUser.getId(), updatedUser.getLoginId());
        return UserLoginResponse.from(updatedUser, accessToken, refreshToken, accessTokenCookie, refreshTokenCookie);
    }

    /**
     * 사용자 상태를 검증합니다.
     * 
     * @param user 검증할 사용자 엔티티
     * @throws UserException 계정이 활성 상태가 아닌 경우
     */
    public void validateUserStatus(User user) {
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

    /**
     * 비밀번호를 검증합니다.
     * 
     * @param user        사용자 엔티티
     * @param rawPassword 입력받은 원본 비밀번호
     * @return 비밀번호 일치 여부
     */
    private boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * 
     * @param uuid 현재 로그인한 사용자의 UUID
     * @return 사용자 정보 응답 DTO
     * @throws UserException 사용자가 존재하지 않는 경우
     */
    public UserInfoResponse getUserInfo(String uuid) {
        User user = findUserByUuid(uuid);
        return UserInfoResponse.from(user);
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     * 
     * @param uuid 사용자 UUID
     * @return 사용자 엔티티
     * @throws UserException 사용자가 존재하지 않는 경우
     */
    private User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자 ID: {}", uuid);
                    return new UserException(UserErrorCode.USER_NOT_FOUND);
                });
    }

    /**
     * 사용자 정보를 업데이트합니다.
     * 
     * @param uuid    사용자 UUID
     * @param request 사용자 정보 업데이트 요청 DTO
     * @return 업데이트된 사용자 정보 응답 DTO
     * @throws UserException 사용자가 존재하지 않거나 데이터 유효성 검증에 실패한 경우
     */
    @Transactional
    public UserInfoResponse updateUserInfo(String uuid, UserUpdateRequest request) {
        User user = findUserByUuid(uuid);

        // 비밀번호 변경 처리
        if (StringUtils.hasText(request.getNewPassword())) {
            if (!StringUtils.hasText(request.getCurrentPassword())) {
                throw new UserException(UserErrorCode.CURRENT_PASSWORD_REQUIRED);
            }

            // 현재 비밀번호 확인
            if (!validatePassword(user, request.getCurrentPassword())) {
                throw new UserException(UserErrorCode.INVALID_PASSWORD);
            }

            // 새 비밀번호로 변경
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
        }

        // 이름 업데이트
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        // 닉네임 업데이트
        if (StringUtils.hasText(request.getNickname()) && !request.getNickname().equals(user.getNickname())) {
            validateNickname(request.getNickname());
            user.setNickname(request.getNickname());
        }

        // 이메일 업데이트
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            validateDuplicateEmail(request.getEmail());
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // 이메일이 변경되면 인증 상태 초기화
        }

        // 전화번호 업데이트
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }

        // 프로필 이미지 URL 업데이트
        if (StringUtils.hasText(request.getProfileImageUrl())) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return UserInfoResponse.from(updatedUser);
    }

    /**
     * 사용자 계정을 삭제(비활성화)합니다.
     * 실제로 데이터베이스에서 삭제하지 않고 상태만 DELETED로 변경합니다.
     * 
     * @param uuid     사용자 UUID
     * @param password 계정 삭제 확인용 비밀번호
     * @return 삭제된 사용자 엔티티
     * @throws UserException 사용자가 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void deleteUserAccount(String uuid) {
        User user = findUserByUuid(uuid);
        
        // 소셜 연동용 데이터 초기화
        List<SocialUser> socialUsers = socialUserRepository.findByUuid(uuid);
        for (SocialUser socialUser : socialUsers) {
            socialUser.deleteSocialUser();
        }
        socialUserRepository.saveAll(socialUsers);

        // 소셜 연동용 이메일 초기화
        user.deleteUser();
        userRepository.save(user);
        
        return;
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 탈퇴 유저 복구 기능 -> state값만 살리기

    // 탈퇴 유저 재가입용 프로세스 -> 기존 계정 provider, email null처리
    
}