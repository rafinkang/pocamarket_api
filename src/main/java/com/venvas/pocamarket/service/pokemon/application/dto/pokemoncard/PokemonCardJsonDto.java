package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.PokemonAbilityJsonDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.PokemonAttackJsonDto;

import java.util.List;

/**
 * Json Parsing Dto
 * 변수명이 json 키 값과 동일
 */
public record PokemonCardJsonDto(
        String code,
        String name,
        String name_ko,
        String element,
        String type,
        String subtype,
        Integer health,
        String set,
        String pack,
        List<PokemonAttackJsonDto> attacks,
        Integer retreatCost,
        String weakness,
        List<PokemonAbilityJsonDto> abilities,
        String evolvesFrom,
        String rarity)
{}
