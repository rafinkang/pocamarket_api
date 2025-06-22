package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcgTradeRequestRepository extends JpaRepository<TcgTradeRequest, Long> {
}