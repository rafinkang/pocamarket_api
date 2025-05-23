package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.service.pokemon.application.dto.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.PokeCardSearchListFilterCondition;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 포켓몬 카드 서비스
 * 포켓몬 카드 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PokemonCardService {
    
    private final PokemonCardRepository pokemonCardRepository;
    
    /**
     * 모든 포켓몬 카드를 조회
     * @return 전체 포켓몬 카드 목록
     */
    public List<PokemonCard> getAllCards() {
        return pokemonCardRepository.findAll();
    }
    
    /**
     * 카드 코드로 특정 포켓몬 카드를 조회
     * @param code 카드 코드
     * @return 조회된 포켓몬 카드 (Optional)
     */
    public Optional<PokemonCard> getCardByCode(String code) {
        return pokemonCardRepository.findByCode(code);
    }
    
    /**
     * 한글 이름으로 포켓몬 카드를 검색
     * @param name 검색할 한글 이름
     * @return 검색된 포켓몬 카드 목록
     */
    public List<PokemonCard> searchByName(String name) {
        return pokemonCardRepository.findByNameKoContaining(name);
    }
    
    /**
     * 특정 속성의 포켓몬 카드를 조회
     * @param element 포켓몬 속성
     * @return 조회된 포켓몬 카드 목록
     */
    public List<PokemonCard> getCardsByElement(String element) {
        return pokemonCardRepository.findByElement(element);
    }
    
    /**
     * 특정 확장팩의 포켓몬 카드를 조회
     * @param packSet 확장팩 이름
     * @return 조회된 포켓몬 카드 목록
     */
    public List<PokemonCard> getCardsByPackSet(String packSet) {
        return pokemonCardRepository.findByPackSet(packSet);
    }
    
    /**
     * 특정 레어도의 포켓몬 카드를 조회
     * @param rarity 카드 레어도
     * @return 조회된 포켓몬 카드 목록
     */
    public List<PokemonCard> getCardsByRarity(String rarity) {
        return pokemonCardRepository.findByRarity(rarity);
    }

    /**
     * 필터값으로 카드를 조회
     * @param condition 카드 필터값
     * @return 조회된 카드 목록
     */
    public List<PokemonCardListDto> getListData(PokeCardSearchListFilterCondition condition) {
        return pokemonCardRepository.searchFilterList(condition);
    }
} 