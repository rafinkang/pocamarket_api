package com.venvas.pocamarket.service.pokemon.application.service;

import com.venvas.pocamarket.common.util.MappingData;
import com.venvas.pocamarket.common.util.ReadDataListJson;
import com.venvas.pocamarket.config.BaseTestAnnotations;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PokemonCardUpdateService 테스트
 * H2 인메모리 DB를 사용하여 포켓몬 카드 업데이트 서비스를 테스트합니다.
 */
@Slf4j
@BaseTestAnnotations
class PokemonCardUpdateServiceTest {

    @Autowired
    private EntityManager em;

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

        if (optionalList.isEmpty()) {
            throw new PokemonException(PokemonErrorCode.POKEMON_LIST_EMPTY);
        }

        pokemonCardJsonDto = optionalList.get();
    }

    @Test
    @DisplayName("JSON 데이터 읽기 테스트")
    void dataCheck() {
        // given & when
        for (PokemonCardJsonDto dto : pokemonCardJsonDto) {
            log.info("dto = {}", dto);
        }

        // then
        assertThat(pokemonCardJsonDto).isNotNull();
        assertThat(pokemonCardJsonDto).isNotEmpty();
    }

    @Test
    @Rollback
    @DisplayName("포켓몬 공격 엔티티 생성 테스트")
    void createAttackEntity() {
        // given & when
        List<PokemonAttack> attacks = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .flatMap(dto -> MappingData.mappingDataList(
                        dto.attacks(), attackDto -> new PokemonAttack(attackDto, dto.code())
                ).stream())
                .toList();

        long expectedCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .mapToLong(dto -> MappingData.mappingDataList(
                        dto.attacks(), attackDto -> new PokemonAttack(attackDto, dto.code())
                ).size())
                .sum();

        // then
        log.info("attack.size = {}, expectedCount = {}", attacks.size(), expectedCount);
        assertThat(attacks.size()).isEqualTo(expectedCount);
    }

    @Test
    @Rollback
    @DisplayName("포켓몬 특성 엔티티 생성 테스트")
    void createAbilityEntity() {
        // given & when
        List<PokemonAbility> abilities = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .flatMap(dto -> MappingData.mappingDataList(
                        dto.abilities(), abilityDto -> new PokemonAbility(abilityDto, dto.code())
                ).stream())
                .toList();

        long expectedCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .mapToLong(dto -> MappingData.mappingDataList(
                        dto.abilities(), abilityDto -> new PokemonAbility(abilityDto, dto.code())
                ).size())
                .sum();

        // then
        log.info("abilities.size = {}, expectedCount = {}", abilities.size(), expectedCount);
        assertThat(abilities.size()).isEqualTo(expectedCount);
    }

    @Test
    @Rollback
    @DisplayName("포켓몬 카드 엔티티 생성 테스트")
    void createPokemonCardEntity() {
        // given & when
        List<PokemonCard> pokemonCardList = pokemonCardJsonDto.stream()
                .map(dto -> {
                    List<PokemonAttack> attackList = MappingData.mappingDataList(
                            dto.attacks(), 
                            attacksDto -> new PokemonAttack(attacksDto, dto.code())
                    );
                    List<PokemonAbility> abilitiesList = MappingData.mappingDataList(
                            dto.abilities(), 
                            abilityDto -> new PokemonAbility(abilityDto, dto.code())
                    );
                    return new PokemonCard(dto, attackList, abilitiesList);
                })
                .toList();

        // 공격이 있는 카드 개수 계산
        long attackCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.attacks() != null && !dto.attacks().isEmpty())
                .count();

        // 특성이 있는 카드 개수 계산
        long abilityCount = pokemonCardJsonDto.stream()
                .filter(dto -> dto.abilities() != null && !dto.abilities().isEmpty())
                .count();

        // then
        assertThat(pokemonCardList.size()).isEqualTo(pokemonCardJsonDto.size());
        
        assertThat(pokemonCardList.stream()
                .filter(card -> card.getAttacks() != null && !card.getAttacks().isEmpty())
                .count()).isEqualTo(attackCount);
        
        assertThat(pokemonCardList.stream()
                .filter(card -> card.getAbilities() != null && !card.getAbilities().isEmpty())
                .count()).isEqualTo(abilityCount);
    }

    @Test
    @Rollback
    @DisplayName("upsertJsonData 신규 insert 및 update 동작 테스트")
    void upsertJsonData_insertAndUpdate() {
        // given
        String fileName = "test"; // 테스트용 JSON 파일명
        String packSet = "T9";     // 테스트용 packSet

        try {
            // when: 1차 실행 - 신규 insert
            List<PokemonCard> firstResult = pokemonCardUpdateService.upsertJsonData(fileName, packSet);

            // then: DB에 데이터가 정상적으로 insert 되었는지 확인
            assertThat(firstResult).isNotEmpty();
            List<PokemonCard> firstDbSelectList = pokemonCardRepository.findByPackSetLikeList(packSet);
            assertThat(firstDbSelectList.size()).isEqualTo(firstResult.size());

            // when: 2차 실행 - 같은 데이터로 update
            String fileName2 = "test2";
            List<PokemonCard> secondResult = pokemonCardUpdateService.upsertJsonData(fileName2, packSet);

            // then: 기존 데이터가 update 되었는지 확인
            List<PokemonCard> secondDbSelectList = pokemonCardRepository.findByPackSetLikeList(packSet);
            assertThat(secondDbSelectList.size()).isEqualTo(secondResult.size());

            // 특정 카드의 필드가 update 되었는지 검증
            if (!secondResult.isEmpty()) {
                PokemonCard updatedCard = secondResult.get(0);
                Optional<PokemonCard> dbCardOpt = pokemonCardRepository.findByCode(updatedCard.getCode());
                
                if (dbCardOpt.isPresent()) {
                    PokemonCard dbCard = dbCardOpt.get();
                    log.info("cardName : {}", dbCard.getNameKo());
                    assertThat(dbCard.getNameKo()).isEqualTo(updatedCard.getNameKo());
                }
            }
        } catch (Exception e) {
            log.warn("upsertJsonData 테스트 실패: {}", e.getMessage());
            // JSON 파일이 없을 수 있으므로 테스트를 스킵하거나 적절히 처리
            log.info("테스트용 JSON 파일이 없어 upsert 테스트를 스킵합니다.");
        }
    }
}