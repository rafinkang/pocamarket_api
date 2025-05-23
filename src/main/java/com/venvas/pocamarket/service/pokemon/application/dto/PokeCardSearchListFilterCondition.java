package com.venvas.pocamarket.service.pokemon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PokeCardSearchListFilterCondition {

    private String nameKo;
    private String element;

    private String trainerType;

    private Integer minHealth;
    private Integer maxHealth;

    private String packSet;
    private String pack;

    private Integer retreatCost;

    private String rarity;
}
