package com.venvas.pocamarket.service.pokemon.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbilityDto {
    private String name;
    private String name_ko;
    private String effect;
    private String effect_ko;
}
