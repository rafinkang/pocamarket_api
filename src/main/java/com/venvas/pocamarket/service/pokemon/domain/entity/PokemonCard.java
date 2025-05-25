package com.venvas.pocamarket.service.pokemon.domain.entity;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Index;

/**
 * 포켓몬 카드 엔티티
 * 포켓몬 카드의 기본 정보를 저장하는 테이블과 매핑되는 엔티티 클래스
 */
@Entity
@Table(name = "pokemon_card", indexes = {
    @Index(name = "idx_pokemon_card_code", columnList = "code")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PokemonCard {
    
    /**
     * 카드 고유 식별자
     * 자동 증가하는 기본키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;
    
    /**
     * 카드 코드
     * 카드의 고유 식별 코드 (예: SWSH1-001)
     */
    @Column(nullable = false, length = 50)
    private String code;
    
    /**
     * 전국도감 번호
     * 포켓몬의 전국도감 번호
     */
    @Column(name = "dex_id")
    private Integer dexId;
    
    /**
     * 그룹 코드
     * 포켓몬의 그룹 분류 코드
     */
    @Column(name = "dex_group")
    private Integer dexGroup;
    
    /**
     * 영문 이름
     * 포켓몬의 영문 이름
     */
    @Column(length = 50)
    private String name;
    
    /**
     * 한글 이름
     * 포켓몬의 한글 이름
     */
    @Column(name = "name_ko", length = 50)
    private String nameKo;
    
    /**
     * 포켓몬 타입
     * 포켓몬의 속성 (예: 물, 불, 전기 등)
     */
    @Column(length = 50)
    private String element;
    
    /**
     * 카드 대분류
     * 카드의 주요 분류 (기본값: POKEMON)
     * POKEMON: 포켓몬 카드
     * TRAINER: 트레이너 카드
     */
    @Column(nullable = false, length = 50)
    private String type = "POKEMON";
    
    /**
     * 카드 소분류
     * 카드의 세부 분류 (기본값: BASIC)
     * BASIC: 기본 포켓몬
     * STAGE_1: 1단계 진화
     * STAGE_2: 2단계 진화
     * SUPPORTER: 서포터
     * ITEM: 아이템
     */
    @Column(nullable = false, length = 50)
    private String subtype = "BASIC";
    
    /**
     * 체력
     * 포켓몬의 HP
     */
    private Integer health;
    
    /**
     * 확장팩 이름
     * 카드가 속한 확장팩 세트의 이름
     */
    @Column(name = "pack_set", nullable = false, length = 50)
    private String packSet;
    
    /**
     * 팩 이름
     * 카드가 속한 팩의 이름
     */
    @Column(nullable = false, length = 50)
    private String pack;
    
    /**
     * 후퇴 비용
     * 포켓몬이 후퇴하는데 필요한 에너지 수
     */
    @Column(name = "retreat_cost")
    private Integer retreatCost;
    
    /**
     * 약점
     * 포켓몬의 약점 속성
     */
    @Column(length = 50)
    private String weakness;
    
    /**
     * 진화 전 포켓몬
     * 진화 전 포켓몬의 이름 (진화형 포켓몬의 경우)
     */
    @Column(name = "evolves_from", length = 50)
    private String evolvesFrom;
    
    /**
     * 레어도
     * 카드의 희귀도 (기본값: COMMON)
     * COMMON: 일반
     * UNCOMMON: 비일반
     * RARE: 레어
     * RARE_HOLO: 홀로그램 레어
     */
    @Column(nullable = false, length = 50)
    private String rarity = "COMMON";

    /**
     * 포켓몬 카드의 기술 목록
     * 카드가 보유한 기술들의 컬렉션
     */
    @OneToMany(mappedBy = "pokemonCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonAttack> attacks = new ArrayList<>();

    /**
     * 포켓몬 카드의 특성 목록
     * 카드가 보유한 특성들의 컬렉션
     */
    @OneToMany(mappedBy = "pokemonCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonAbility> abilities = new ArrayList<>();

    public PokemonCard(PokemonCardDto dto, List<PokemonAttack> attackList, List<PokemonAbility> abilityList) {
        this.code = dto.code();
        this.name = dto.name();
        this.nameKo = dto.name_ko();
        this.element = dto.element();
        this.type = dto.type();
        this.subtype = dto.subtype();
        this.health = dto.health();
        this.packSet = dto.set();
        this.pack = dto.pack();
        this.retreatCost = dto.retreatCost();
        this.weakness = dto.weakness();
        this.evolvesFrom = dto.evolvesFrom();
        this.rarity = dto.rarity();
        this.attacks = attackList;
        this.abilities = abilityList;
    }
} 