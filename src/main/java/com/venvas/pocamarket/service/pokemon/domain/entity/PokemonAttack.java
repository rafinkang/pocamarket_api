package com.venvas.pocamarket.service.pokemon.domain.entity;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.PokemonAttackJsonDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포켓몬 카드 기술 엔티티
 * 포켓몬 카드의 기술 정보를 저장하는 테이블과 매핑되는 엔티티 클래스
 */
@Entity
@Table(name = "pokemon_attack")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PokemonAttack {
    
    /**
     * 기술 고유 식별자
     * 자동 증가하는 기본키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attack_id")
    private Long attackId;
    
    /**
     * 카드 코드
     * 포켓몬 카드의 고유 식별 코드
     */
    @Column(name = "card_code", nullable = false, length = 50)
    private String cardCode;
    
    /**
     * 기술 영문 이름
     */
    @Column(length = 50)
    private String name;
    
    /**
     * 기술 한글 이름
     */
    @Column(name = "name_ko", length = 50)
    private String nameKo;
    
    /**
     * 기술 효과 (영문)
     */
    @Column(length = 256)
    private String effect;
    
    /**
     * 기술 효과 (한글)
     */
    @Column(name = "effect_ko", length = 512)
    private String effectKo;
    
    /**
     * 데미지
     */
    @Column(nullable = false, length = 50)
    private String damage = "";
    
    /**
     * 기술 비용
     * 기본값: COLORLESS
     */
    @Column(nullable = false, length = 100)
    private String cost = "COLORLESS";
    
    /**
     * 포켓몬 카드와의 관계
     * 카드 코드를 통해 포켓몬 카드와 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_code", nullable = false, insertable = false, updatable = false)
    private PokemonCard pokemonCard;

    public PokemonAttack(PokemonAttackJsonDto dto, String cardCode) {
        this.cardCode = cardCode;
        this.name = dto.name();
        this.nameKo = dto.name_ko();
        this.effect = dto.effect();
        this.effectKo = dto.effect_ko();
        this.damage = dto.damage();
        this.cost = String.join(",", dto.cost());
    }
}