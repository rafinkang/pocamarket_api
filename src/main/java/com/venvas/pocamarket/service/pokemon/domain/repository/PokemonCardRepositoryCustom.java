package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.PokeCardSearchListFilterCondition;

import java.util.List;

public interface PokemonCardRepositoryCustom {
    List<PokemonCardListDto> searchFilterList(PokeCardSearchListFilterCondition condition);
}
