package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
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
    PokemonCardService pokemonCardService;
    @Autowired
    PokemonCardRepositoryImpl pokemonCardRepository;

    /**
     * url : /list
     * default page = 0
     * default size = 30
     */
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
     * url : /list?size=99
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

    /**
     * url : /list?size=30&page=2
     */
    @Test
    public void 페이지_이동() {
        int page = 2;
        final int MAX_SIZE = 30;

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, null, null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(page, MAX_SIZE);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        int pageNumber = result.getNumber();

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("code = {}, name = {}", dto.getCode(), dto.getNameKo());
        }

        log.info("pageNumber = {}", pageNumber);
        assertThat(pageNumber).isEqualTo(page);
    }

    /**
     * url : /list?nameKo=버터플
     */
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

    /**
     * url : /list?type=POKEMON
     * 소문자여도 상관없음
     */
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
     * url : /list?subtype=ITEM
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

    /**
     * url : /list?type=pokemon&subtype=basic
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_1
     */
    @Test
    public void 타입과_다중서브타입검색() {
        String type = "POKEMON      ";
        String subType = "BASIC,STAGE_1";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFilterSearchCondition condition = new PokemonCardListFilterSearchCondition(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardService.getListData(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("name = {}, type = {}, subType = {}", dto.getNameKo(), dto.getType(), dto.getSubtype());
            assertThat(dto.getType()).isEqualTo(type.trim());
            assertThat(split).contains(dto.getSubtype());
        }
    }

    /**
     * url : /list?nameKo=이생해&type=pokemon&subType=basic,stage_1,stage_2
     */
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

    /**
     * url : /list?nameKo=리자&type=pokemon&subType=basic,stage_1,stage_2&element=fire
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_1,stage_2&element=grass,fire
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_2&element=grass,fire&packSet=A2
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_2&element=grass,water&packSet=A2&pack=dialga
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_2&element=grass,water&packSet=A2&pack=dialga&rarity=common
     */
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

    /**
     * url : /list?nameKo=꾸꾸리&type=pokemon&subType=basic,stage_2&element=grass,water&packSet=A2&pack=dialga&rarity=common
     */
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

    /**
     * url : /list?type=pokemon&subType=basic,stage_2&sort=code,desc
     */
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
     * url : /list?type=pokemon&subType=basic,stage_2&sort=rarity,desc&sort=code,asc
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