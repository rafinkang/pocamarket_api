package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.domain.entity.TcgTrade;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 카드 교환 카드 코드 Repository
 */
@Repository
public interface TcgTradeCardCodeRepository extends JpaRepository<TcgTradeCardCode, Long> {
    
    /**
     * 거래 ID로 카드 코드들 조회
     * 
     * @param trade 거래 엔티티
     * @return 카드 코드 목록
     */
    List<TcgTradeCardCode> findByTrade(TcgTrade trade);
    
    /**
     * 거래 ID로 카드 코드들 삭제
     * 
     * @param trade 거래 엔티티
     */
    void deleteByTrade(TcgTrade trade);
} 