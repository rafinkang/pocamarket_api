package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 거래 엔티티
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tcg_trade")
public class TcgTrade {
    /** 거래번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    /** 포켓몬게임 친구코드 */
    @Column(name = "tcg_code", nullable = false, length = 100)
    private String tcgCode;

    /** 작성자 UUID */
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    /** 작성자 닉네임 */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 거래상태 */
    @Column(name = "status")
    private Integer status = 1;

    /** 생성시간 */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 최종 수정시간 */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "sorted_at", nullable = false)
    private LocalDateTime sortedAt;

    /** 참조 추가 */
    @OneToMany(mappedBy = "trade", fetch = FetchType.LAZY)
    private List<TcgTradeCardCode> tcgTradeCardCodes = new ArrayList<>();
    
    /**
     * 거래 엔티티 생성 (ID와 타임스탬프 제외)
     */
    public TcgTrade(String tcgCode, String uuid, String nickname, Integer status) {
        this.tcgCode = tcgCode;
        this.uuid = uuid;
        this.nickname = nickname;
        this.status = status;
    }
    
    /**
     * 거래 정보 업데이트 (tcgCode 수정)
     * 
     * @param tcgCode 새로운 tcgCode
     */
    public void updateTcgCode(String tcgCode) {
        this.tcgCode = tcgCode;
    }

    public void refresh() {
        this.sortedAt = LocalDateTime.now();
    }

    public void updateStatus(Integer status) {
        this.status = status;
    }
} 