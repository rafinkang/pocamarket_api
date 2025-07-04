package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.common.dto.PageResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFormDto;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAbility;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAttack;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @DisplayName("디폴트 정렬 확인")
    public void checkDefaultSorting() {
        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, null, null, null, null, null, null);
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
    @DisplayName("초과 페이지 사이즈")
    public void checkExceededPageSize() {
        int pageSize = 99;
        final int MAX_SIZE = 30;

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, null, null, null, null, null, null);
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
    @DisplayName("페이지 이동")
    public void checkPageNavigation() {
        int page = 2;
        final int MAX_SIZE = 30;

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, null, null, null, null, null, null);
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
    @DisplayName("이름검색")
    public void searchByName() {
        String searchName = "버터플";
        PageRequest pageRequest = PageRequest.of(0, 5);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(searchName, null, null, null, null, null, null);
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
    @DisplayName("타입검색")
    public void searchByType() {
        String type = "POKEMON";
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, null, null, null, null, null);
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
    @DisplayName("서브타입검색")
    public void searchBySubtype() {
        String subType = "ITEM";
        PageRequest pageRequest = PageRequest.of(0, 5);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, null, subType, null, null, null, null);
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
    @DisplayName("타입과 단일서브타입검색")
    public void searchByTypeAndSingleSubtype() {
        String type = "POKEMON";
        String subType = "BASIC";
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, null, null, null, null);
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
    @DisplayName("타입과 다중서브타입검색")
    public void searchByTypeAndMultipleSubtypes() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, null, null, null, null);
        PageResponse<PokemonCardListDto> result = pokemonCardService.getListData(condition, pageRequest);

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
    @DisplayName("이름 타입 서브타입")
    public void searchByNameTypeAndSubtypes() {
        String name = "이상해";
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1,STAGE_2";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(name, type, subType, null, null, null, null);
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
    @DisplayName("이름 타입 서브타입 속성")
    public void searchByNameTypeSubtypesAndElement() {
        String name = "리자";
        String type = "POKEMON";
        String subType = "BASIC,STAGE_1,STAGE_2";
        String element = "FIRE";
        String[] split = subType.split(",");
        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(name, type, subType, element, null, null, null);
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
    @DisplayName("타입 서브타입 다중속성")
    public void searchByTypeSubtypesAndMultipleElements() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,FIRE";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, element, null, null, null);
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
    @DisplayName("타입 서브타입 다중속성 확장팩")
    public void searchByTypeSubtypesElementsAndPackSet() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,FIRE";
        String packSet = "A2";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, element, packSet, null, null);
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
    @DisplayName("타입 서브타입 다중속성 확장팩 팩")
    public void searchByTypeSubtypesElementsPackSetAndPack() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,WATER";
        String packSet = "A2";
        String pack = "Dialga";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, element, packSet, pack, null);
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
    @DisplayName("타입 서브타입 다중속성 확장팩 팩 레어도")
    public void searchByTypeSubtypesElementsPackSetPackAndRarity() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        String element = "GRASS,WATER";
        String packSet = "A2";
        String pack = "Dialga";
        String rarity = "COMMON";

        String[] subtypeSplit = subType.split(",");
        String[] elementSplit = element.split(",");

        PageRequest pageRequest = PageRequest.of(0, 30);

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, element, packSet, pack, rarity);
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
    @DisplayName("이름 타입 서브타입 다중속성 확장팩 팩 레어도")
    public void searchByAllParameters() {
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

        PokemonCardListFormDto condition = new PokemonCardListFormDto(name, type, subType, element, packSet, pack, rarity);
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
    @DisplayName("정렬")
    public void checkSorting() {
        String type = "POKEMON";
        String subType = "STAGE_1,STAGE_2";
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "code"));

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, null, null, null, null);
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
    @DisplayName("멀티정렬")
    public void checkMultipleSorting() {
        String type = "POKEMON";
        String subType = "BASIC,STAGE_2";
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "rarity").and(Sort.by(Sort.Direction.ASC, "code")));

        PokemonCardListFormDto condition = new PokemonCardListFormDto(null, type, subType, null, null, null, null);
        Page<PokemonCardListDto> result = pokemonCardRepository.searchFilterList(condition, pageRequest);

        for (PokemonCardListDto dto : result.getContent()) {
            log.info("code = {}, name = {}, rarity = {}", dto.getCode(), dto.getNameKo(), dto.getRarity());
        }
    }

    @Test
    @DisplayName("확장팩 이름으로 검색 리스트 출력 json data")
    public void searchByPackSetAndPrintJsonData() {
        List<PokemonCard> a1List = pokemonCardRepository.findByPackSetLikeList("A1");

        for (PokemonCard c : a1List) {
            log.info("card : code = {}, name = {}, pack = {}", c.getCode(), c.getNameKo(), c.getPack());
            for (PokemonAttack a : c.getAttacks()) {
                log.info("\t -> attack :  cardCode = {}, name : {}", a.getCardCode(), a.getNameKo());
            }

            for (PokemonAbility a : c.getAbilities()) {
                log.info("\t -> ability :  cardCode = {}, name : {}", a.getCardCode(), a.getNameKo());
            }
        }

        assertThat(a1List).isNotNull();
        assertThat(a1List.isEmpty()).isFalse();
    }
}