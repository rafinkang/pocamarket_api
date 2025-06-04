package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 정보를 관리하는 엔티티
 * 사용자의 기본 정보와 로그인/비밀번호 이력을 포함
 */
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 사용자 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 닉네임 (Unique)
     */
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * 이메일 (Unique)
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;

    /**
     * 전화번호
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
     * ACTIVE: 활성, INACTIVE: 비활성, SUSPENDED: 일시정지, DELETED: 탈퇴 등
     */
    @Column(name = "status")
    @Builder.Default
    private Integer statusCode = UserStatus.ACTIVE.getCode();
    
    @Transient // DB에 저장하지 않는 필드
    private transient UserStatus status;

    /**
     * 사용자 권한 등급
     * LV01: 일반 사용자, LV02: 프리미엄, LV03: VIP, ADMIN: 관리자
     */
    @Column(name = "grade")
    @Builder.Default
    private Integer gradeCode = UserGrade.LV01.getCode();
    
    @Transient // DB에 저장하지 않는 필드
    private transient UserGrade grade;

    /**
     * 이메일 인증 여부
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * 생성 시간 (CreationTimestamp로 자동 관리)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 업데이트 시간 (UpdateTimestamp로 자동 관리)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 사용자의 로그인 이력 목록
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserLoginHistory> loginHistories = new ArrayList<>();
    

    /**
     * 사용자의 비밀번호 변경 이력 목록
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserPasswordHistory> passwordHistories = new ArrayList<>();
    
    /**
     * 로그인 시도를 기록
     * 
     * @param ipAddress IP 주소
     * @param userAgent 사용자 에이전트 정보
     * @param successful 로그인 성공 여부
     * @param failReason 실패 이유 (실패시에만 사용)
     * @return 생성된 로그인 이력 엔티티
     */
    public UserLoginHistory recordLoginAttempt(String ipAddress, String userAgent, boolean successful, String failReason) {
        UserLoginHistory loginHistory = new UserLoginHistory();
        loginHistory.setUser(this);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setUserAgent(userAgent);
        loginHistory.setSuccess(successful);
        loginHistory.setFailReason(failReason);
        
        this.loginHistories.add(loginHistory);
        
        // 로그인 성공 시 마지막 로그인 시간 업데이트 (현재는 사용하지 않음)
        // 필요한 경우 로그인 이력을 통해 확인할 수 있음
        
        return loginHistory;
    }
    
    /**
     * 로그인 시도를 기록 (성공 케이스)
     * 
     * @param ipAddress IP 주소
     * @param userAgent 사용자 에이전트 정보
     * @return 생성된 로그인 이력 엔티티
     */
    public UserLoginHistory recordLoginAttempt(String ipAddress, String userAgent, boolean successful) {
        return recordLoginAttempt(ipAddress, userAgent, successful, null);
    }

    /**
     * 사용자 생성 정적 팩토리 메소드
     * 
     * @param request        사용자 생성 요청 DTO
     * @param encodedPassword 암호화된 비밀번호
     * @return 생성된 User 엔티티
     */
    public static User createFromRequest(UserCreateRequest request, String encodedPassword) {
        User user = User.builder()
                .uuid(UUID.randomUUID().toString())
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .emailVerified(false)
                .build();
                
        // 열거형 값 명시적 설정
        user.setStatus(UserStatus.ACTIVE);
        user.setGrade(UserGrade.LV01);
        
        return user;
    }
    
    /**
     * 열거형 상태 값을 조회하는 게터 메소드
     * @return 사용자 상태 열거형
     */
    public UserStatus getStatus() {
        if (status == null && statusCode != null) {
            status = UserStatus.fromDbCode(statusCode);
        }
        return status;
    }
    
    /**
     * 열거형 상태 값을 설정하는 세터 메소드
     * @param status 사용자 상태 열거형
     */
    public void setStatus(UserStatus status) {
        this.status = status;
        this.statusCode = UserStatus.toCode(status);
    }
    
    /**
     * 열거형 등급 값을 조회하는 게터 메소드
     * @return 사용자 등급 열거형
     */
    public UserGrade getGrade() {
        if (grade == null && gradeCode != null) {
            grade = UserGrade.fromDbCode(gradeCode);
        }
        return grade;
    }
    
    /**
     * 열거형 등급 값을 설정하는 세터 메소드
     * @param grade 사용자 등급 열거형
     */
    public void setGrade(UserGrade grade) {
        this.grade = grade;
        this.gradeCode = UserGrade.toCode(grade);
    }
    
    /**
     * 이메일 인증 여부 값을 조회하는 게터 메소드
     * @return 이메일 인증 여부
     */
    public boolean isEmailVerified() {
        return this.emailVerified != null && this.emailVerified;
    }
    
    /**
     * 이메일 인증 여부 설정
     * @param emailVerified 이메일 인증 여부
     */
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    /**
     * 비밀번호를 변경합니다.
     * @param password 새 비밀번호(해시 처리된 값)
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 사용자 이름을 변경합니다.
     * @param name 새 사용자 이름
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 닉네임을 변경합니다.
     * @param nickname 새 닉네임
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    /**
     * 이메일을 변경합니다.
     * @param email 새 이메일
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * 전화번호를 변경합니다.
     * @param phone 새 전화번호
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * 프로필 이미지 URL을 변경합니다.
     * @param profileImageUrl 새 프로필 이미지 URL
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
