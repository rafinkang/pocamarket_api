package com.venvas.pocamarket.service.user.domain.repository;

import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 소셜 로그인 사용자 정보 저장소
 * 소셜 로그인 관련 데이터베이스 연산을 담당
 */
@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    
    /**
     * 사용자 UUID로 소셜 로그인 정보 조회
     * 
     * @param uuid 사용자 UUID
     * @return 해당 사용자의 소셜 로그인 정보 리스트
     */
    List<SocialUser> findByUuid(String uuid);
    
    /**
     * 사용자 UUID와 제공자로 소셜 로그인 정보 조회
     * 
     * @param uuid 사용자 UUID
     * @param provider 소셜 로그인 제공자
     * @return 해당 사용자의 특정 제공자 소셜 로그인 정보
     */
    Optional<SocialUser> findByUuidAndProvider(String uuid, String provider);
    
    /**
     * 제공자와 제공자 ID로 소셜 로그인 정보 조회
     * 
     * @param provider 소셜 로그인 제공자
     * @param providerId 제공자에서 제공하는 사용자 ID
     * @return 해당 소셜 로그인 정보
     */
    Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId);
    
    /**
     * 사용자 UUID로 활성 상태의 소셜 로그인 정보 조회
     * 
     * @param uuid 사용자 UUID
     * @return 해당 사용자의 활성 상태 소셜 로그인 정보 리스트
     */
    @Query("SELECT s FROM SocialUser s WHERE s.uuid = :uuid AND s.active = 1")
    List<SocialUser> findActiveByUuid(@Param("uuid") String uuid);
    
    /**
     * 제공자와 제공자 ID로 활성 상태의 소셜 로그인 정보 조회
     * 
     * @param provider 소셜 로그인 제공자
     * @param providerId 제공자에서 제공하는 사용자 ID
     * @return 해당 소셜 로그인 정보
     */
    @Query("SELECT s FROM SocialUser s WHERE s.provider = :provider AND s.providerId = :providerId AND s.active = 1")
    Optional<SocialUser> findActiveByProviderAndProviderId(
            @Param("provider") String provider, 
            @Param("providerId") String providerId);
    
    /**
     * 제공자별 활성 사용자 수 조회
     * 
     * @param provider 소셜 로그인 제공자
     * @return 해당 제공자의 활성 사용자 수
     */
    @Query("SELECT COUNT(s) FROM SocialUser s WHERE s.provider = :provider AND s.active = 1")
    long countActiveByProvider(@Param("provider") String provider);
    
    /**
     * 사용자 UUID로 소셜 로그인 정보 존재 여부 확인
     * 
     * @param uuid 사용자 UUID
     * @return 존재 여부
     */
    boolean existsByUuid(String uuid);
    
    /**
     * 제공자와 제공자 ID로 소셜 로그인 정보 존재 여부 확인
     * 
     * @param provider 소셜 로그인 제공자
     * @param providerId 제공자에서 제공하는 사용자 ID
     * @return 존재 여부
     */
    boolean existsByProviderAndProviderId(String provider, String providerId);
    
    /**
     * 사용자 UUID로 소셜 로그인 정보 삭제
     * 
     * @param uuid 사용자 UUID
     */
    void deleteByUuid(String uuid);
    
    /**
     * 사용자 UUID와 제공자로 소셜 로그인 정보 삭제
     * 
     * @param uuid 사용자 UUID
     * @param provider 소셜 로그인 제공자
     */
    void deleteByUuidAndProvider(String uuid, String provider);
} 