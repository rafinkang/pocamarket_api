package com.venvas.pocamarket.service.trade.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포켓몬게임 친구코드 엔티티
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tcg_code")
public class TcgCode {
    /** 포켓몬게임 친구코드 id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tcg_code_id")
    private Long id;

    /** 포켓몬게임 친구코드 */
    @Column(name = "tcg_code", nullable = false, length = 150)
    private String tcgCode;

    /** UUID */
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    /** 상태값 1:활성 0:비활성 */
    @Column(name = "status")
    private Integer status = 1;

    /** 유저 메모용 */
    @Column(name = "memo", length = 150)
    private String memo;
} 