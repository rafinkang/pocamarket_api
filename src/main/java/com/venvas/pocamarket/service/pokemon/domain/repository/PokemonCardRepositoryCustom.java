package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface PokemonCardRepositoryCustom {
    Page<PokemonCardListDto> searchFilterList(PokemonCardListFilterSearchCondition condition, Pageable pageable);
    Slice<PokemonCardListDto> searchFilterSliceList(PokemonCardListFilterSearchCondition condition, Pageable pageable);
    Optional<PokemonCardDetailDto> findByCodeDetailCard(String code);
}
