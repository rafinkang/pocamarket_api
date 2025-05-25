package com.venvas.pocamarket.service.pokemon.domain.entity;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.AbilityDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포켓몬 카드 특성 엔티티
 * 포켓몬 카드의 특성 정보를 저장하는 테이블과 매핑되는 엔티티 클래스
 */
@Entity
@Table(name = "pokemon_ability")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PokemonAbility {
    
    /**
     * 특성 고유 식별자
     * 자동 증가하는 기본키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ability_id")
    private Long abilityId;
    
    /**
     * 카드 코드
     * 포켓몬 카드의 고유 식별 코드
     */
    @Column(name = "card_code", nullable = false, length = 50)
    private String cardCode;
    
    /**
     * 특성 영문 이름
     */
    @Column(length = 50)
    private String name;
    
    /**
     * 특성 한글 이름
     */
    @Column(name = "name_ko", length = 50)
    private String nameKo;
    
    /**
     * 특성 효과 (영문)
     */
    @Column(length = 256)
    private String effect;
    
    /**
     * 특성 효과 (한글)
     */
    @Column(name = "effect_ko", length = 512)
    private String effectKo;
    
    /**
     * 포켓몬 카드와의 관계
     * 카드 코드를 통해 포켓몬 카드와 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_code", insertable = false, updatable = false)
    private PokemonCard pokemonCard;

    public PokemonAbility(AbilityDto dto, String cardCode) {
        this.cardCode = cardCode;
        this.name = dto.name();
        this.nameKo = dto.name_ko();
        this.effect = dto.effect();
        this.effectKo = dto.effect_ko();
    }
} 