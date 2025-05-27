package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional(readOnly = true)
@Slf4j
class PokemonCardRepositoryImplTest {

    @Autowired
    PokemonCardRepositoryImpl pokemonCardRepository;

    @Test
    public void 디폴트_정렬_확인() {
        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, null, null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto pokemonCardListDto : result.getContent()) {
            log.info("name : {}, code : {}", pokemonCardListDto.getNameKo(), pokemonCardListDto.getCode());
        }

        for (Sort.Order order : result.getSort()) {
            log.info("direction : {}, property: {}", order.getDirection(), order.getProperty());
        }
        result.getSort().stream().forEach(order -> {
            assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
            assertThat(order.getProperty()).isEqualTo("code");
        });
    }

    /**
     * 음수 페이지는 PageRequest 객체 생성에서 에러
     */
    @Test
    public void 초과_페이지_사이즈() {
        int pageSize = 99;
        final int MAX_SIZE = 30;

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, null, null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        int resultSize = result.getContent().size();

        log.info("resultSize = {}", resultSize);
        assertThat(resultSize).isEqualTo(MAX_SIZE);
    }


    @Test
    public void 이름검색() {
        String searchName = "버터플";
        PageRequest pageRequest = PageRequest.of(0, 5);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(searchName, null, null, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto pokemonCardListDto : result.getContent()) {
            assertThat(pokemonCardListDto.getNameKo()).isEqualTo(searchName);
        }
    }

    @Test
    public void 타입검색() {
        String type = "POKEMON";
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, null, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

//        log.info("result.content.size = {}", result.getContent().size());
        for (PokemonCardListDto pokemonCardListDto : result.getContent()) {
            log.info("name = {}, Type = {}", pokemonCardListDto.getNameKo(), pokemonCardListDto.getType());
            assertThat(pokemonCardListDto.getType()).isEqualTo(type);
        }
    }

    /**
     * 타입없이 서브타입으로만 검색하면 검색이 무시됨
     */
    @Test
    public void 서브타입검색() {
        String subType = "ITEM";
        PageRequest pageRequest = PageRequest.of(0, 5);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, null, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto pokemonCardListDto : result.getContent()) {
            log.info("name = {}, subType = {}", pokemonCardListDto.getNameKo(), pokemonCardListDto.getSubtype());
            assertThat(pokemonCardListDto.getSubtype()).isNotEqualTo(subType);
        }
    }

    @Test
    public void 타입과_단일서브타입검색() {
        String type = "POKEMON";
        String subType = "BASIC";
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}", dto.getNameKo(), dto.getType(), dto.getSubtype());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(dto.getSubtype()).isEqualTo(subType);
        }
    }

    @Test
    public void 타입과_다중서브타입검색() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}", dto.getNameKo(), dto.getType(), dto.getSubtype());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(split).contains(dto.getSubtype());
        }
    }

    @Test
    public void 이름_타입_서브타입() {
        String name = "이상해";
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1,STAGE_2";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(name, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}", dto.getNameKo(), dto.getType(), dto.getSubtype());
            assertThat(dto.getNameKo().matches("^" + name + ".*$")).isTrue();
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(split).contains(dto.getSubtype());
        }
    }

    @Test
    public void 이름_타입_서브타입_속성() {
        String name = "리자";
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1,STAGE_2";
        String element = "FIRE";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(name, type, subType, element, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement());
            assertThat(dto.getNameKo().matches("^" + name + ".*$")).isTrue();
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(split).contains(dto.getSubtype());
            assertThat(dto.getElement()).isEqualTo(element);
        }
    }

    @Test
    public void 타입_서브타입_다중속성() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,FIRE";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, element, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(subtypeSplit).contains(dto.getSubtype());
            assertThat(elementSplit).contains(dto.getElement());
        }
    }

    @Test
    public void 타입_서브타입_다중속성_확장팩() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,FIRE";
        String packSet = "A2";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, element, packSet, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}, packSet = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement(), dto.getPackSet());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(subtypeSplit).contains(dto.getSubtype());
            assertThat(elementSplit).contains(dto.getElement());
            assertThat(dto.getPackSet().matches(".*\\(" + packSet + "\\)$")).isTrue();
        }
    }

    @Test
    public void 타입_서브타입_다중속성_확장팩_팩() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,WATER";
        String packSet = "A2";
        String pack = "Dialga";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, element, packSet, pack, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}, packSet = {}, pack = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement(), dto.getPackSet(), dto.getPack());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(subtypeSplit).contains(dto.getSubtype());
            assertThat(elementSplit).contains(dto.getElement());
            assertThat(dto.getPackSet().matches(".*\\(" + packSet + "\\)$")).isTrue();
            assertThat(dto.getPack().matches(pack)).isTrue();
        }
    }

    @Test
    public void 타입_서브타입_다중속성_확장팩_팩_레어도() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,WATER";
        String packSet = "A2";
        String pack = "Dialga";
        String rarity = "COMMON";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, element, packSet, pack, rarity);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}, packSet = {}, pack = {}, rarity = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement(), dto.getPackSet(), dto.getPack(), dto.getRarity());
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(subtypeSplit).contains(dto.getSubtype());
            assertThat(elementSplit).contains(dto.getElement());
            assertThat(dto.getPackSet().matches(".*\\(" + packSet + "\\)$")).isTrue();
            assertThat(dto.getPack().matches(pack)).isTrue();
            assertThat(dto.getRarity().matches(rarity)).isTrue();
        }
    }

    @Test
    public void 이름_타입_서브타입_다중속성_확장팩_팩_레어도() {
        String name = "꾸꾸리";
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,WATER";
        String packSet = "A2";
        String pack = "Dialga";
        String rarity = "COMMON";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(name, type, subType, element, packSet, pack, rarity);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}, element = {}, packSet = {}, pack = {}, rarity = {}", dto.getNameKo(), dto.getType(), dto.getSubtype(), dto.getElement(), dto.getPackSet(), dto.getPack(), dto.getRarity());
            assertThat(dto.getNameKo().matches(name)).isTrue();
            assertThat(dto.getType()).isEqualTo(type);
            assertThat(subtypeSplit).contains(dto.getSubtype());
            assertThat(elementSplit).contains(dto.getElement());
            assertThat(dto.getPackSet().matches(".*\\(" + packSet + "\\)$")).isTrue();
            assertThat(dto.getPack().matches(pack)).isTrue();
            assertThat(dto.getRarity().matches(rarity)).isTrue();
        }
    }

    @Test
    public void 정렬() {
        String type = "POKEMON";
        String subType = "STAGE_1,STAGE_2";
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "code"));

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("code = {}, name = {}, type = {}, subType = {}", dto.getCode(), dto.getNameKo(), dto.getType(), dto.getSubtype());
        }
    }

    /**
     * 특정 컬러만 정렬 됨
     */
    @Test
    public void 멀티정렬() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "rarity").and(Sort.by(Sort.Direction.ASC, "code")));

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("code = {}, name = {}, rarity = {}", dto.getCode(), dto.getNameKo(), dto.getRarity());
        }
    }
}