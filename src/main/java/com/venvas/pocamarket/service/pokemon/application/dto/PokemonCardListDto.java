package com.venvas.pocamarket.service.pokemon.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PokemonCardListDto {

    @NotEmpty
    private String code;
    private String nameKo;
    private String element;
    private String type;
    private String subtype;
    private String packSet;
    private String pack;
    private String rarity;

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
