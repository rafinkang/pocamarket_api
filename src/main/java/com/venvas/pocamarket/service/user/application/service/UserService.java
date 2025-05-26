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
     * @throws UserException 로그인 ID나 이메일이 이미 사용 중인 경우
     */
    public User createUser(UserCreateRequest request) {
        log.info("사용자 생성 시작: loginId={}", request.getLoginId());

        // 중복 체크
        validateDuplicateUser(request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 사용자 엔티티 생성 및 저장
        User user = User.createFromRequest(request, encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("사용자 생성 완료: userId={}, loginId={}", savedUser.getId(), savedUser.getLoginId());
        return savedUser;

        // 예외는 상위 레이어로 전파됨
    }

    /**
     * 로그인 ID 중복 여부를 확인
     * 
     * @throws UserException 중복된 사용자가 존재하는 경우
     */
    private void validateDuplicateUser(UserCreateRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new UserException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }


}