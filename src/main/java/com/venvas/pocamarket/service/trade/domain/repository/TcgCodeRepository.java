package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TcgCodeRepository extends JpaRepository<TcgCode, Long> {
    Long countByUuidAndStatus(String uuid, int status);
    Optional<TcgCode> findByUuid(String uuid);

    // uuid와 status로 여러 결과 검색
    List<TcgCode> findAllByUuidAndStatus(String uuid, int status);

    @Query("select t from TcgCode t where t.id = :tcg_code_id and t.uuid = :uuid and t.status = :status")
    Optional<TcgCode> findUpdateCode(@Param("tcg_code_id") Long id, @Param("uuid") String uuid, @Param("status") int status);
}
