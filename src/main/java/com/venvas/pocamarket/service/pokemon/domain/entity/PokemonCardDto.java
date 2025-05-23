package com.venvas.pocamarket.service.pokemon.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokemonCardDto {

    private String code;
    private String name;
    private String name_ko;
    private String element;
    private String type;
    private String subtype;
    private Integer health;
    private String set;
    private String pack;
    private List<AttacksDto> attacks;
    private Integer retreatCost;
    private String weakness;
    private List<AbilityDto> abilities;
    private String evolvesFrom;
    private String rarity;
}
