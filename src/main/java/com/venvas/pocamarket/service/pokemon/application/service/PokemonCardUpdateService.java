package com.venvas.pocamarket.service.pokemon.application.service;

import com.venvas.pocamarket.common.util.MappingData;
import com.venvas.pocamarket.common.util.ReadDataListJson;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardJsonDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.*;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonException;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 포켓몬 카드 데이터 업데이트를 담당하는 서비스 클래스
 * JSON 파일에서 카드 데이터를 읽어와 데이터베이스에 저장하는 기능을 제공
 */
@Service  // Spring 서비스 빈으로 등록
@Transactional  // 모든 메서드에 트랜잭션 적용
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성
@Slf4j  // 로깅을 위한 Lombok 어노테이션
public class PokemonCardUpdateService {

    // JPA Repository 주입 (생성자 주입)
    private final PokemonCardRepository pokemonCardRepository;

    /**
     * JSON 파일에서 포켓몬 카드 데이터를 읽어와 데이터베이스에 저장하는 메서드
     * @param fileName JSON 파일의 버전 (파일명에 사용됨)
     * @return ApiResponse<List<PokemonCard>> 저장된 카드 목록과 처리 결과
     */
    public List<PokemonCard> upsertJsonData(String fileName, String packSet) {
        // Json Data
        List<PokemonCardJsonDto> pokemonJsonDto = getPokemonJsonDto(fileName);
        // DB Data
        List<PokemonCard> searchList = pokemonCardRepository.findByPackSetLikeList(packSet);

        // code 기준으로 기존 카드 맵핑
        Map<String, PokemonCard> existingCardMap = new HashMap<>();
        if (searchList != null) {
            for (PokemonCard card : searchList) {
                existingCardMap.put(card.getCode(), card);
            }
        }

        List<PokemonCard> insertList = new ArrayList<>();
        List<PokemonCard> updateList = new ArrayList<>();

        // 각 DTO를 엔티티로 변환하여 리스트에 추가
        for (PokemonCardJsonDto dto : pokemonJsonDto) {
            PokemonCard newCard = mappingCardData(dto);
            PokemonCard existing = existingCardMap.get(dto.code());

            if (existing != null) {
                // update
                existing.updateFrom(newCard);
                updateList.add(existing);
            } else {
                // insert
                insertList.add(newCard);
            }
        }

        List<PokemonCard> result = new ArrayList<>();

        if (!updateList.isEmpty()) {
            result.addAll(pokemonCardRepository.saveAll(updateList));
        }
        // 신규 카드만 저장, 기존 카드는 커밋 시점에 자동 update
        if (!insertList.isEmpty()) {
            result.addAll(pokemonCardRepository.saveAll(insertList));
        }

        return result;
    }

    /**
     * JSON 파일을 읽어서 PokemonCardDto 리스트로 변환 없으면 Exception throw
     */
    private List<PokemonCardJsonDto> getPokemonJsonDto(String fileName) {
        ReadDataListJson<PokemonCardJsonDto> readJson = new ReadDataListJson<>(fileName);
        return readJson
                .readJson(PokemonCardJsonDto.class)
                .getJsonList()
                .orElseThrow(() -> new PokemonException(PokemonErrorCode.JSON_DATA_EMPTY));
    }

    /**
     * PokemonCardDto를 PokemonCard 엔티티로 변환하는 메서드
     * @param c 변환할 PokemonCardDto
     * @return 변환된 PokemonCard 엔티티
     */
    private PokemonCard mappingCardData(PokemonCardJsonDto c) {
        // 공격 데이터와 특성 데이터를 각각 엔티티로 변환
        List<PokemonAttack> attackList = new ArrayList<>(MappingData.mappingDataList(c.attacks(), d -> new PokemonAttack(d, c.code())));
        List<PokemonAbility> abilityList = new ArrayList<>(MappingData.mappingDataList(c.abilities(), d -> new PokemonAbility(d, c.code())));

        // 변환된 데이터로 PokemonCard 엔티티 생성하여 반환
        return new PokemonCard(c, attackList, abilityList);
    }
}
