package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TcgTradeRequestRepository extends JpaRepository<TcgTradeRequest, Long>, TcgTradeRequestRepositoryCustom {


    @Query("SELECT tr FROM TcgTradeRequest tr WHERE tr.id = :id AND tr.trade.id = :tradeId")
    Optional<TcgTradeRequest> findByIdAndTradeId(@Param("id") Long id, @Param("tradeId") Long tradeId);

    @Query("SELECT tr FROM TcgTradeRequest tr WHERE tr.id = :id AND tr.trade.id = :tradeId AND tr.uuid = :uuid")
    Optional<TcgTradeRequest> findByIdAndTradeIdAndUuid(@Param("id") Long id, @Param("tradeId") Long tradeId, @Param("uuid") String uuid);
}