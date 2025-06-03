package com.venvas.pocamarket.service.user.domain.repository;

import com.venvas.pocamarket.service.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(String uuid);
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
}