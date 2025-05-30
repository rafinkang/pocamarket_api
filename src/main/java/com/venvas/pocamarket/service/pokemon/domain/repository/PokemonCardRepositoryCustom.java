package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PokemonCardRepositoryCustom {
    // list 데이터를 Page 객체로 리턴
    Page<PokemonCardListDto> searchFilterList(PokemonCardListFilterSearchCondition condition, Pageable pageable);
    // list 데이터를 Slice 객체로 리턴
    Slice<PokemonCardListDto> searchFilterSliceList(PokemonCardListFilterSearchCondition condition, Pageable pageable);
    Optional<PokemonCardDetailDto> findByCodeDetailCard(String code);
    List<PokemonCard> findByPackSetLikeList(String packSet);
}
