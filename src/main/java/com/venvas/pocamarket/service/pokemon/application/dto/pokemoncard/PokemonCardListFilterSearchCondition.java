package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PokemonCardListFilterSearchCondition {

    private String nameKo;
    private String type;
    private String subType;

    private String element;

    private String packSet;
    private String pack;

    private String rarity;
}
