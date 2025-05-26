package com.venvas.pocamarket.common.util;

import com.venvas.pocamarket.common.exception.data.JsonParsingException;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
@Slf4j
class ReadDataListJsonTest {

    /**
     * 이름 or 경로 잘못된 경우 실패
     */
    @Test
    public void jsonUrlFail() {
        ReadDataListJson<PokemonCardDto> readDataListJson = new ReadDataListJson<>("prmm");

        assertThatThrownBy(() -> readDataListJson.readJson(PokemonCardDto.class))
                .isInstanceOf(JsonParsingException.class);
    }

    /**
     * 파일 읽기에 성공 했을 경우
     */
    @Test
    public void jsonUrlSuccess() {
        // 자바 제네릭은 컴파일 시점에서는 무슨 타입인지 알지만 런타임 때는 모름
        ReadDataListJson<PokemonCardDto> readDataListJson = new ReadDataListJson<>("promo");
        List<PokemonCardDto> dtos = readDataListJson.readJson(PokemonCardDto.class)
                .getJsonList()
                .orElseGet(() -> null);

        assertThat(dtos).isNotNull(); // null이 아님
        assertThat(dtos.size()).isGreaterThan(0); // 사이즈가 0보다 큼
        assertThat(dtos.get(0)).isInstanceOf(PokemonCardDto.class); // 객체 타입 체크
    }
}