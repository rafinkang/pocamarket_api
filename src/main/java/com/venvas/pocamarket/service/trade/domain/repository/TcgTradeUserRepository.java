package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TcgTradeUserRepository extends JpaRepository<TcgTradeUser, String> {
    Optional<TcgTradeUser> findByUuid(String uuid);
} 