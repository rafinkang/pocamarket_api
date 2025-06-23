package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 카드 교환 Repository
 */
@Repository
public interface TcgTradeRepository extends JpaRepository<TcgTrade, Long>, TcgTradeRepositoryCustom, QuerydslPredicateExecutor<TcgTrade> {
    @Query("SELECT t FROM TcgTrade t JOIN FETCH t.tcgTradeCardCodes WHERE t.id = :tradeId")
    Optional<TcgTrade> findByIdWithCardCodes(@Param("tradeId") Long tradeId);
    Integer countByUuidAndStatusIn(String uuid, List<Integer> status);
} 