package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TcgTradeRequestRepository extends JpaRepository<TcgTradeRequest, Long>, TcgTradeRequestRepositoryCustom {

    @Query("SELECT tr FROM TcgTradeRequest tr WHERE tr.id = :id AND tr.trade.id = :tradeId AND tr.status != 0")
    Optional<TcgTradeRequest> findByIdAndTradeId(@Param("id") Long id, @Param("tradeId") Long tradeId);

    @Query("SELECT EXISTS (SELECT 1 FROM TcgTradeRequest tr WHERE tr.trade.id = :tradeId AND tr.uuid = :uuid AND tr.requestCardCode = :requestCardCode AND tr.status != 0)")
    boolean existsByTradeIdAndUuidAndRequestCardCode(@Param("tradeId") Long tradeId, @Param("uuid") String uuid, @Param("requestCardCode") String requestCardCode);

    @Query("SELECT EXISTS (SELECT 1 FROM TcgTradeRequest tr WHERE tr.trade.id = :tradeId AND tr.status != 0)")
    boolean existsByTradeId(@Param("tradeId") Long tradeId);

    Integer countByUuidAndStatusIn(String uuid, List<Integer> status);
}