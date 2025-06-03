package com.venvas.pocamarket.service.user.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.user.domain.entity.QRefreshToken;
import com.venvas.pocamarket.service.user.domain.entity.RefreshToken;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RefreshToken 엔티티에 대한 QueryDSL 커스텀 리포지토리 구현체
 */
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {

    private final EntityManager entityManager;
    
    /**
     * 사용자 UUID로 유효한(만료되지 않은) 리프레쉬 토큰을 조회합니다.
     * 
     * @param uuid 사용자 UUID
     * @param currentTime 현재 시간
     * @return 유효한 리프레쉬 토큰 목록
     */
    @Override
    public Optional<RefreshToken> findValidTokensByUuid(String uuid, LocalDateTime currentTime) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QRefreshToken refreshToken = QRefreshToken.refreshToken;
        
        // String 타입 uuid로 변경되었으므로 문자열 비교 수행
        return Optional.ofNullable(queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.uuid.eq(uuid)
                        .and(refreshToken.revoked.eq(false))
                        .and(refreshToken.expiresAt.gt(currentTime)))
                .fetchFirst());
    }
    
    /**
     * 만료된 리프레쉬 토큰을 모두 조회합니다.
     * 
     * @param currentTime 현재 시간
     * @return 만료된 리프레쉬 토큰 목록
     */
    @Override
    public List<RefreshToken> findAllExpiredTokens(LocalDateTime currentTime) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QRefreshToken refreshToken = QRefreshToken.refreshToken;
        
        return queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.revoked.eq(true)
                        .or(refreshToken.expiresAt.loe(currentTime)))
                .fetch();
    }
    
    /**
     * 사용자의 모든 리프레쉬 토큰을 강제 만료 처리합니다.
     * 
     * @param uuid 사용자 UUID
     * @return 업데이트된 행 수
     */
    @Override
    public long revokeAllTokensByUuid(String uuid) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QRefreshToken refreshToken = QRefreshToken.refreshToken;
        
        // String 타입 uuid로 변경되었으므로 문자열 비교 수행
        return queryFactory
                .update(refreshToken)
                .set(refreshToken.revoked, true)
                .where(refreshToken.uuid.eq(uuid))
                .execute();
    }
}
