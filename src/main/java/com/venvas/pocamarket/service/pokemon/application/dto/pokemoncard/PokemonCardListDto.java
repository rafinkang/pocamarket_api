package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PokemonCardListDto {

    @NotEmpty
    private final String code;
    private final String nameKo;
    private final String element;
    private final String type;
    private final String subtype;
    private final String packSet;
    private final String pack;
    private final String rarity;

    @QueryProjection
    public PokemonCardListDto(String code, String nameKo, String element, String type, String subtype, String packSet, String pack, String rarity) {
        this.code = code;
        this.nameKo = nameKo;
        this.element = element;
        this.type = type;
        this.subtype = subtype;
        this.packSet = packSet;
        this.pack = pack;
        this.rarity = rarity;
    }
}
