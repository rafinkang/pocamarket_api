package com.venvas.pocamarket.service.pokemon.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttacksDto {
    private String name;
    private String name_ko;
    private String effect;
    private String effect_ko;
    private String damage;
    private List<String> cost;
}
