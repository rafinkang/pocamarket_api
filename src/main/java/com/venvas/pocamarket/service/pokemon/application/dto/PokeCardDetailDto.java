package com.venvas.pocamarket.service.pokemon.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/*
    TODO : 기술, 특성 관련 데이터도 넣어줘야함.
 */

@Getter
public class PokeCardDetailDto {

    private String code;
    private String nameKo;
    private String element;
    private String type;
    private String subtype;
    private Integer health;
    private String packSet;
    private String pack;
    private Integer retreatCost;
    private String weakness;
    private String evolvesFrom;
    private String rarity;

    @QueryProjection
    public PokeCardDetailDto(String code, String nameKo, String element, String type, String subtype, Integer health, String packSet, String pack, Integer retreatCost, String weakness, String evolvesFrom, String rarity) {
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
    }
}
