package com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PokemonAttackDetailDto {
    private final String nameKo;
    private final String effectKo;
    private final String damage;
    private final String cost;

    @QueryProjection
    public PokemonAttackDetailDto(String nameKo, String effectKo, String damage, String cost) {
        this.nameKo = nameKo;
        this.effectKo = effectKo;
        this.damage = damage;
        this.cost = cost;
    }
}
