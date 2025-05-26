package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 정보를 관리하는 엔티티
 * 사용자의 기본 정보와 로그인/비밀번호 이력을 포함
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    /**
     * 사용자 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 사용자 UUID (Unique)
     */
    @Column(name = "uuid", nullable = false, unique = true, length = 50)
    private String uuid;

    /**
     * 로그인 ID (Unique)
     */
    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    /**
     * 비밀번호 (해시값)
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 사용자 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 사용자 닉네임
     */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /**
     * 사용자 이메일
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 사용자 전화번호
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    /**
     * 사용자 상태
     * 1: 활성, 0: 탈퇴 등
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 사용자 권한 등급
     * 99: 관리자, 1: 일반 사용자 등
     */
    @Column(name = "grade")
    private Integer grade = 1;

    /**
     * 이메일 인증 여부
     */
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    /**
     * 계정 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 정보 수정 일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 사용자의 로그인 이력 목록
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLoginHistory> loginHistories = new ArrayList<>();

    /**
     * 사용자의 비밀번호 변경 이력 목록
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPasswordHistory> passwordHistories = new ArrayList<>();
} 