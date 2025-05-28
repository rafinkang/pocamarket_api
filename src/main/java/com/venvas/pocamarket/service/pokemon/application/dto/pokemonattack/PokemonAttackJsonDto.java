package com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack;


import java.util.List;

//private String name;
//private String name_ko;
//private String effect;
//private String effect_ko;
//private String damage;
//private List<String> cost;

public record PokemonAttackJsonDto(String name, String name_ko, String effect, String effect_ko, String damage, List<String> cost) {}
