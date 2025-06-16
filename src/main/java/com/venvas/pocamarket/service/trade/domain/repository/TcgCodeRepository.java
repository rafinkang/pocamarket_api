package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TcgCodeRepository extends JpaRepository<TcgCode, Long> {
    Optional<TcgCode> findByUuid(String uuid);

        // uuid와 status로 여러 결과 검색
        List<TcgCode> findAllByUuidAndStatus(String uuid, Integer status);
}
