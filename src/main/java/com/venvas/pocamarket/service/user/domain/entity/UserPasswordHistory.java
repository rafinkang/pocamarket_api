package com.venvas.pocamarket.service.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 비밀번호 변경 이력을 관리하는 엔티티
 * 비밀번호 변경 시점과 변경된 비밀번호 해시값을 기록
 */
@Entity
@Table(name = "user_password_history")
@Getter
@Setter
@NoArgsConstructor
public class UserPasswordHistory {
    /**
     * 비밀번호 이력 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 비밀번호를 변경한 사용자 정보 (FK)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_user_password_history"))
    private User user;

    /**
     * 변경된 비밀번호 해시값
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 비밀번호 변경 일시
     */
    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;
} 