package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.application.dto.UserLoginRequest;
import com.venvas.pocamarket.service.user.application.dto.UserLoginResponse;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 * 사용자 생성, 조회, 수정 등의 기능을 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    // 닉네임에 사용할 수 없는 금지된 단어 목록
    private static final List<String> PROHIBITED_NICKNAME_WORDS = Arrays.asList(
            "admin", "root", "system", "manager", "superuser", "administrator", "관리자");
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 생성
     * 
     * @param request 사용자 생성 요청 DTO
     * @return 생성된 사용자 엔티티
     * @throws UserException 유효성 검증에 실패한 경우 (중복 ID, 이메일 등)
     */
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
        String token = generateToken(updatedUser);
        
        log.info("로그인 성공: userId={}, loginId={}", updatedUser.getId(), updatedUser.getLoginId());
        return UserLoginResponse.from(updatedUser, token);
    }
    
    /**
     * 사용자 상태를 검증합니다.
     * 
     * @param user 검증할 사용자 엔티티
     * @throws UserException 계정이 활성 상태가 아닌 경우
     */
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
    
    /**
     * 비밀번호를 검증합니다.
     * 
     * @param user 사용자 엔티티
     * @param rawPassword 입력받은 원본 비밀번호
     * @return 비밀번호 일치 여부
     */
    private boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    
    /**
     * JWT 토큰을 생성합니다.
     * 실제 애플리케이션에서는 JWT 라이브러리를 사용하여 구현합니다.
     * 
     * @param user 토큰을 생성할 사용자 엔티티
     * @return 생성된 JWT 토큰
     */
    private String generateToken(User user) {
        // 실제 구현에서는 JWT 토큰 생성 로직을 구현
        // 예: 사용자 ID, 권한 등을 포함하여 서명된 토큰 생성
        return "sample_jwt_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}