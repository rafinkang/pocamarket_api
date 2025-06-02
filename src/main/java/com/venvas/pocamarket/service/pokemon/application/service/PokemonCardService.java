package com.venvas.pocamarket.service.pokemon.application.service;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFormDto;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonException;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 포켓몬 카드 서비스
 * 포켓몬 카드 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PokemonCardService {

    private final PokemonCardRepository pokemonCardRepository;

    /**
     * 카드 코드로 특정 포켓몬 카드를 조회
     * @param code 카드 코드
     * @return 조회된 포켓몬 카드 (Optional)
     */
    public PokemonCardDetailDto getCardByCode(String code) {
        return pokemonCardRepository.findByCodeDetailCard(code)
                .orElseThrow(() -> new PokemonException(PokemonErrorCode.POKEMON_NOT_FOUND, "유효하지 않은 코드입니다."));
    }

    /**
     * 필터값으로 카드를 조회
     * @param condition 카드 필터값
     * @return 조회된 카드 목록
     */
    public Page<PokemonCardListDto> getListData(PokemonCardListFormDto condition, Pageable pageable) {
        Page<PokemonCardListDto> listDto = pokemonCardRepository.searchFilterList(condition, pageable);
        noDataListCheck(listDto, condition);
        return listDto;
    }

    public Slice<PokemonCardListDto> getListDataSlice(PokemonCardListFormDto condition, Pageable pageable) {
        Slice<PokemonCardListDto> listDto = pokemonCardRepository.searchFilterSliceList(condition, pageable);
        noDataListCheck(listDto, condition);
        return listDto;
    }

    private void noDataListCheck(Slice<PokemonCardListDto> listDto, PokemonCardListFormDto condition) {
        if(listDto == null || listDto.getContent().isEmpty()) {
            log.warn("포켓몬 리스트 데이터 조회 실패, filterCondition : {}", condition);
            throw new PokemonException(PokemonErrorCode.POKEMON_LIST_EMPTY);
        }
    }
} 