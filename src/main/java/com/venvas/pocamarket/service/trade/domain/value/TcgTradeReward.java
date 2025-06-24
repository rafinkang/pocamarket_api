package com.venvas.pocamarket.service.trade.domain.value;

/**
 * TCG 거래 완료 시 지급되는 보상 상수
 */
public final class TcgTradeReward {
    
    private TcgTradeReward() {
        // 인스턴스 생성 방지
    }
    
    /** 거래 완료 시 지급되는 포인트 */
    public static final Integer TRADE_COMPLETE_POINT = 100;
    
    /** 거래 완료 시 지급되는 경험치 */
    public static final Integer TRADE_COMPLETE_EXP = 50;
    
    /** 첫 거래 완료 시 추가 보너스 포인트 */
    public static final Integer FIRST_TRADE_BONUS_POINT = 200;
    
    /** 첫 거래 완료 시 추가 보너스 경험치 */
    public static final Integer FIRST_TRADE_BONUS_EXP = 100;
    
    
} 