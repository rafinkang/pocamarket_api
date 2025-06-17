package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 카드 교환 카드 코드 Repository
 */
@Repository
public interface TcgTradeCardCodeRepository extends JpaRepository<TcgTradeCardCode, Long> {
} 