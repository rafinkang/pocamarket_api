package com.venvas.pocamarket.service.user.domain.repository;

import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리프레쉬 토큰 리포지토리
 * 리프레쉬 토큰 엔티티에 대한 데이터 액세스를 제공합니다.
 * QueryDSL을 사용하여 복잡한 쿼리를 처리합니다.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

    /**
     * 토큰 문자열로 리프레쉬 토큰을 조회합니다.
     * 
     * @param token 토큰 문자열
     * @return 리프레쉬 토큰 (Optional)
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * 사용자 UUID로 리프레쉬 토큰 목록을 조회합니다.
     * 
     * @param uuid 사용자 UUID
     * @return 리프레쉬 토큰 목록
     */
    List<RefreshToken> findByUuid(String uuid);
}
