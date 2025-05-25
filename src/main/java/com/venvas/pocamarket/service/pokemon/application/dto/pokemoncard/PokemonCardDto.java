package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.AbilityDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.AttacksDto;

import java.util.List;


//private String code;
//private String name;
//private String name_ko;
//private String element;
//private String type;
//private String subtype;
//private Integer health;
//private String set;
//private String pack;
//private List<AttacksDto> attacks;
//private Integer retreatCost;
//private String weakness;
//private List<AbilityDto> abilities;
//private String evolvesFrom;
//private String rarity;

public record PokemonCardDto(
        String code,
        String name,
        String name_ko,
        String element,
        String type,
        String subtype,
        Integer health,
        String set,
        String pack,
        List<AttacksDto> attacks,
        Integer retreatCost,
        String weakness,
        List<AbilityDto> abilities,
        String evolvesFrom,
        String rarity)
{}
