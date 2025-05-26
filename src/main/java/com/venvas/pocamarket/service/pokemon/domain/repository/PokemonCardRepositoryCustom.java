package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokeCardSearchListFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PokemonCardRepositoryCustom {
    Page<PokemonCardListDto> searchFilterList(PokeCardSearchListFilterCondition condition, Pageable pageable);
}
