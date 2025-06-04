package com.venvas.pocamarket.service.user.domain.repository;

import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * RefreshToken 엔티티에 대한 QueryDSL 커스텀 리포지토리 인터페이스
 */
public interface RefreshTokenRepositoryCustom {

    /**
     * 사용자 UUID로 유효한(만료되지 않은) 리프레쉬 토큰을 조회합니다.
     * 
     * @param uuid        사용자 UUID
     * @param currentTime 현재 시간
     * @return 유효한 리프레쉬 토큰 목록
     */
    Optional<RefreshToken> findValidTokensByUuid(String uuid, Date currentTime);

    /**
     * 만료된 리프레쉬 토큰을 모두 조회합니다.
     * 
     * @param currentTime 현재 시간
     * @return 만료된 리프레쉬 토큰 목록
     */
    List<RefreshToken> findAllExpiredTokens(Date currentTime);

    /**
     * 사용자의 모든 리프레쉬 토큰을 강제 만료 처리합니다.
     * 
     * @param uuid 사용자 UUID
     * @return 업데이트된 행 수
     */
    long revokeAllTokensByUuid(String uuid);
}
