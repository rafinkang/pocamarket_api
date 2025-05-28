package com.venvas.pocamarket.service.pokemon.application.dto.pokemonability;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PokemonAbilityDetailDto {

    private final String nameKo;
    private final String effectKo;

    @QueryProjection
    public PokemonAbilityDetailDto(String nameKo, String effectKo) {
        this.nameKo = nameKo;
        this.effectKo = effectKo;
    }
}