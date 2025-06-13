package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * 거래 엔티티
 */
@Getter
@NoArgsConstructor
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
    @Column(name = "created_at")
    private Date createdAt;

    /** 최종 수정시간 */
    @Column(name = "updated_at")
    private Date updatedAt;
} 