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

    @Query("SELECT tr FROM TcgTradeRequest tr WHERE tr.id = :id AND tr.trade.id = :tradeId")
    Optional<TcgTradeRequest> findByIdAndTradeId(@Param("id") Long id, @Param("tradeId") Long tradeId);

    boolean existsByTradeIdAndUuidAndRequestCardCode(Long tradeId, String uuid, String requestCardCode);

    Integer countByUuidAndTradeStatusIn(String uuid, List<Integer> status);
}