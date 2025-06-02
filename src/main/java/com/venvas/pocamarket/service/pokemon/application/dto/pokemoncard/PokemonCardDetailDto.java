package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import com.querydsl.core.annotations.QueryProjection;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.PokemonAbilityDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.PokemonAttackDetailDto;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PokemonCardDetailDto {

    private final String code;
    private final String nameKo;
    private final String element;
    private final String type;
    private final String subtype;
    private final Integer health;
    private final String packSet;
    private final String pack;
    private final Integer retreatCost;
    private final String weakness;
    private final String evolvesFrom;
    private final String rarity;
    private final List<PokemonAttackDetailDto> attackList;
    private final List<PokemonAbilityDetailDto> abilityList;

    @QueryProjection
    public PokemonCardDetailDto(String code, String nameKo, String element, String type, String subtype, Integer health, String packSet, String pack, Integer retreatCost, String weakness, String evolvesFrom, String rarity, List<PokemonAttackDetailDto> attackList, List<PokemonAbilityDetailDto> abilityList) {
        this.code = code;
        this.nameKo = nameKo;
        this.element = element;
        this.type = type;
        this.subtype = subtype;
        this.health = health;
        this.packSet = packSet;
        this.pack = pack;
        this.retreatCost = retreatCost;
        this.weakness = weakness;
        this.evolvesFrom = evolvesFrom;
        this.rarity = rarity;
        this.attackList = attackList;
        this.abilityList = abilityList;
    }
}
