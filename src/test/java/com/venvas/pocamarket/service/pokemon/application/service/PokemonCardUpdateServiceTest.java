package com.venvas.pocamarket.service.pokemon.application.service;

import com.venvas.pocamarket.common.util.MappingData;
import com.venvas.pocamarket.common.util.ReadDataListJson;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardJsonDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAbility;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAttack;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonException;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@Transactional
class PokemonCardUpdateServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private PokemonCardUpdateService pokemonCardUpdateService;

    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    private List<PokemonCardJsonDto> pokemonCardJsonDto;

    @BeforeEach
    void beforeEach() {
        ReadDataListJson<PokemonCardJsonDto> readJson = new ReadDataListJson<>("promo");
        Optional<List<PokemonCardJsonDto>> optionalList = readJson
                .readJson(PokemonCardJsonDto.class)
                .getJsonList();

        if(optionalList.isEmpty()) throw new PokemonException(PokemonErrorCode.POKEMON_LIST_EMPTY);

        pokemonCardJsonDto = optionalList.get();
    }

    @Test
    public void dataCheck() {
        for (PokemonCardJsonDto dto : pokemonCardJsonDto) {
            log.info("dto = {}", dto);
        }
        assertThat(pokemonCardJsonDto).isNotNull();
    }

    @Test
    @Rollback
    public void createAttackEntity() {
        List<PokemonAttack> attacks = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .flatMap(dto -> MappingData.mappingDataList(
                                dto.attacks(), attackDto -> new PokemonAttack(attackDto, dto.code())
                        ).stream())
                .toList();

        long count = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .mapToLong(dto -> MappingData.mappingDataList(
                        dto.attacks(), attackDto -> new PokemonAttack(attackDto, dto.code())
                ).size())
                .sum();


        log.info("attack.size = {}, count = {}", attacks.size(), count);
        assertThat(attacks.size()).isEqualTo(count);
    }

    @Test
    @Rollback
    public void createAbilityEntity() {
        List<PokemonAbility> abilities = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .flatMap(dto -> MappingData.mappingDataList(
                        dto.abilities(), abilityDto -> new PokemonAbility(abilityDto, dto.code())
                ).stream())
                .toList();

        long count = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .mapToLong(dto -> MappingData.mappingDataList(
                        dto.abilities(), abilityDto -> new PokemonAbility(abilityDto, dto.code())
                ).size())
                .sum();

        log.info("abilities.size = {}, count = {}", abilities.size(), count);
        assertThat(abilities.size()).isEqualTo(count);
    }
    
    @Test
    @Rollback
    public void createPokemonCardEntity() {

        // PokemonCard Entity Create
        List<PokemonCard> pokemonCardList = pokemonCardJsonDto.stream()
                .map(dto -> {
                    List<PokemonAttack> attackList = MappingData.mappingDataList(dto.attacks(), attacksDto -> new PokemonAttack(attacksDto, dto.code()));
                    List<PokemonAbility> abilitiesList = MappingData.mappingDataList(dto.abilities(), abilityDto -> new PokemonAbility(abilityDto, dto.code()));
                    return new PokemonCard(dto, attackList, abilitiesList);
                })
                .toList();

        // attacks count
        long attackCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .mapToLong(dto -> 1)
                .sum();

        // ability count
        long abilityCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .mapToLong(dto -> 1)
                .sum();

        // dto, pokemonCard 개수 비교
        assertThat(pokemonCardList.size()).isEqualTo(pokemonCardJsonDto.size());
        // 기술이 있는 카드 개수 비교
        assertThat(pokemonCardList.stream().filter(card -> card.getAttacks() != null).count()).isEqualTo(attackCount);
        // 특성이 있는 카드 개수 비교
        assertThat(pokemonCardList.stream().filter(card -> card.getAbilities() != null).count()).isEqualTo(abilityCount);
    }

    /**
     * saveAll 테스트
     */
    @Test
    @Rollback
    public void updateServiceSaveTest() {
        pokemonCardUpdateService.updateJsonData("test");
        Optional<PokemonCard> findByFirstCodeCard = pokemonCardRepository.findByCode("pa-100");
        Optional<PokemonCard> findByEndCodeCard = pokemonCardRepository.findByCode("pa-103");

        PokemonCard firstCard = null;
        PokemonCard endCard = null;

        if(findByFirstCodeCard.isEmpty()) {
            throw new PokemonException(PokemonErrorCode.POKEMON_NOT_FOUND, "테스트 : 첫번째 데이터 없음");
        } else {
            firstCard = findByFirstCodeCard.get();
        }

        if(findByEndCodeCard.isEmpty()) {
            throw new  PokemonException(PokemonErrorCode.POKEMON_NOT_FOUND, "테스트 : 마지막 데이터 없음");
        } else {
            endCard = findByEndCodeCard.get();
        }

        log.info("firstCard : {}", firstCard);
        log.info("endCard : {}", endCard);

        assertThat(firstCard.getCode()).isEqualTo("pa-100");
        assertThat(endCard.getCode()).isEqualTo("pa-103");
    }
}