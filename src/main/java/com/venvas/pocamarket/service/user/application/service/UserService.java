package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 * 사용자 생성, 조회, 수정 등의 기능을 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 생성
     * 
     * @param request 사용자 생성 요청 DTO
     * @return 생성된 사용자 엔티티
     * @throws UserException 유효성 검증에 실패한 경우 (중복 ID, 이메일 등)
     */
    public User createUser(UserCreateRequest request) {
        log.info("사용자 생성 시작: loginId={}", request.getLoginId());

        // 입력값 유효성 검증
        validateUserData(request);
        
        // 중복 체크
        validateDuplicateUser(request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 사용자 엔티티 생성 및 저장
        User user = User.createFromRequest(request, encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("사용자 생성 완료: userId={}, loginId={}", savedUser.getId(), savedUser.getLoginId());
        return savedUser;
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
        // 로그인 ID 형식 추가 검증 (Bean Validation 외 추가 검증)
        if (request.getLoginId().contains(" ") || request.getLoginId().contains("@")) {
            log.warn("로그인 ID에 공백 또는 특수문자 포함: {}", request.getLoginId());
            throw new UserException(UserErrorCode.INVALID_LOGIN_ID_FORMAT);
        }
        
        // 닉네임에 부적절한 단어가 있는지 검사 (예시)
        String[] prohibitedNicknameWords = {"admin", "root", "system", "manager"};
        String lowercaseNickname = request.getNickname().toLowerCase();
        for (String word : prohibitedNicknameWords) {
            if (lowercaseNickname.contains(word)) {
                log.warn("부적절한 닉네임 단어 포함: {}", request.getNickname());
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
        // 로그인 ID와 이메일 동시 조회로 데이터베이스 쿼리 최소화
        boolean loginIdExists = userRepository.existsByLoginId(request.getLoginId());
        boolean emailExists = false;
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            emailExists = userRepository.existsByEmail(request.getEmail());
        }
        
        // 중복 검사 결과 처리
        if (loginIdExists) {
            log.warn("중복된 로그인 ID 검출: {}", request.getLoginId());
            throw new UserException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
        
        if (emailExists) {
            log.warn("중복된 이메일 검출: {}", request.getEmail());
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }


}