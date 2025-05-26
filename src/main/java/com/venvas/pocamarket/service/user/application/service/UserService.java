package com.venvas.pocamarket.service.user.application.service;


import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        
        // 사용자 엔티티 생성 및 저장
        User user = createUserEntity(request);
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
            throw new UserException("이미 사용 중인 로그인 ID입니다", null);
        }
    }

    /**
     * 사용자 엔티티 생성
     * 요청 데이터를 기반으로 사용자 엔티티를 생성하고 초기값을 설정
     */
    private User createUserEntity(UserCreateRequest request) {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setLoginId(request.getLoginId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // 기본값 설정
        user.setStatus(1);        // 활성 상태
        user.setGrade(1);         // 일반 사용자
        user.setEmailVerified(false);
        
        return user;
    }
} 