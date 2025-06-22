package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 교환 요청 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tcg_trade_request")
public class TcgTradeRequest {
    /** 교환요청번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_request_id")
    private Long id;

    /** 거래번호 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private TcgTrade trade;

    /** 요청자 uuid */
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    /** 요청자 닉네임 */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 포켓몬게임 친구코드 */
    @Column(name = "tcg_code", nullable = false, length = 100)
    private String tcgCode;

    /** 제시 카드 코드 */
    @Column(name = "request_card_code", nullable = false, length = 50)
    private String requestCardCode;

    /** 교환 요청 상태 */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /** 생성시간 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 최종 수정시간 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 교환 요청 엔티티 생성자 (ID와 타임스탬프 제외)
     */
    public TcgTradeRequest(TcgTrade trade, String uuid, String nickname, String tcgCode, String requestCardCode, Integer status) {
        this.trade = trade;
        this.uuid = uuid;
        this.nickname = nickname;
        this.tcgCode = tcgCode;
        this.requestCardCode = requestCardCode;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 