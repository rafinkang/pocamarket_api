package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * 카드 교환 Repository
 */
@Repository
public interface TcgTradeRepository extends JpaRepository<TcgTrade, Long>, TcgTradeRepositoryCustom, QuerydslPredicateExecutor<TcgTrade> {
} 