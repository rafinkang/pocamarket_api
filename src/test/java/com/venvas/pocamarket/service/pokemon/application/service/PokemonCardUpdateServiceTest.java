package com.venvas.pocamarket.service.pokemon.application.service;

import com.venvas.pocamarket.common.util.MappingData;
import com.venvas.pocamarket.common.util.ReadDataListJson;
import com.venvas.pocamarket.config.TestConfig;
import com.venvas.pocamarket.config.TestQueryDslConfig;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Import({TestConfig.class, TestQueryDslConfig.class})
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
        assertThat(pokemonCardList.stream().filter(card -> card.getAttacks() != null && !card.getAttacks().isEmpty()).count()).isEqualTo(attackCount);
        // 특성이 있는 카드 개수 비교
        assertThat(pokemonCardList.stream().filter(card -> card.getAbilities() != null && !card.getAbilities().isEmpty()).count()).isEqualTo(abilityCount);
    }

    /**
     * upsert 테스트
     * 테이블 Auto_Increment 증가 주의 돌리면 값은 저장 안되도 증가 됨.
     * 에러 : 기본 h2 DB에서 연관 관계 이슈 -> 생성 시 자동으로 pokemonCard Id랑 매핑하려고 함, 실제 DB는 code랑 연결 되어있음.
     */
/*    @Test
    @Rollback
    @DisplayName("upsertJsonData 신규 insert 및 update 동작 테스트")
    void upsertJsonData_insertAndUpdate() {
        // given
        String fileName = "test"; // 테스트용 JSON 파일명
        String packSet = "T9";         // 테스트용 packSet

        // 1차 실행: 신규 insert
        List<PokemonCard> firstResult = pokemonCardUpdateService.upsertJsonData(fileName, packSet);

        // then: DB에 데이터가 정상적으로 insert 되었는지 확인
        assertThat(firstResult).isNotEmpty();
        List<PokemonCard> firstDbSelectList = pokemonCardRepository.findByPackSetLikeList(packSet);
        long resultCount = firstDbSelectList.size();
        assertThat(resultCount).isEqualTo(firstResult.size());

        fileName = "test2";
        // 2차 실행: 같은 데이터로 update (값 일부 변경)
        List<PokemonCard> secondResult = pokemonCardUpdateService.upsertJsonData(fileName, packSet);

        // then: 기존 데이터가 update 되었는지 확인 (개수는 동일)
        List<PokemonCard> secondDbSelectList = pokemonCardRepository.findByPackSetLikeList(packSet);
        long dbCountAfterUpdate = secondDbSelectList.size();
        assertThat(dbCountAfterUpdate).isEqualTo(secondResult.size());

        // 추가로, 특정 카드의 필드가 update 되었는지 검증하려면 아래처럼 작성
        PokemonCard updatedCard = secondResult.get(0);
        PokemonCard dbCard = pokemonCardRepository.findByCode(updatedCard.getCode()).orElseThrow();
        log.info("cardName : {}", dbCard.getNameKo());
        assertThat(dbCard.getNameKo()).isEqualTo(updatedCard.getNameKo());
    }*/
}