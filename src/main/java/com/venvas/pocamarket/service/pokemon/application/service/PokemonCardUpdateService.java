package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.AbilityDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.AttacksDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.*;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PokemonCardUpdateService {

    private final PokemonCardRepository pokemonCardRepository;

    public ApiResponse<List<PokemonCard>> updateJsonData(String version) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/sample/" + version + ".json");
        TypeReference<List<PokemonCardDto>> typeReference = new TypeReference<>() {
        };

        try {
            List<PokemonCardDto> pokemonCards = mapper.readValue(inputStream, typeReference);
            log.info("pokemon Card : {}", pokemonCards);

            List<PokemonCard> cardList = new ArrayList<>();

            for (PokemonCardDto card : pokemonCards) {
                cardList.add(mappingCardData(card));
            }

            return ApiResponse.success(pokemonCardRepository.saveAll(cardList),
                    "카드 데이터가 성공적으로 업데이트 되었습니다.");

        } catch (IOException e) {
            return ApiResponse.error(e.getMessage(), "100");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), "101");
        }
    }

    private List<PokemonAbility> mappingAbilityData(List<AbilityDto> aDto, String cardCode) {
        if (aDto == null) return null;

        List<PokemonAbility> list = new ArrayList<>();
        for (AbilityDto a : aDto) {
            list.add(new PokemonAbility(a, cardCode));
        }
        return list;
    }

    private List<PokemonAttack> mappingAttackData(List<AttacksDto> aDto, String cardCode) {
        if (aDto == null) return null;

        List<PokemonAttack> list = new ArrayList<>();
        for (AttacksDto a : aDto) {
            list.add(new PokemonAttack(a, cardCode));
        }
        return list;
    }

    private PokemonCard mappingCardData(PokemonCardDto c) {
        List<PokemonAttack> attackList = mappingAttackData(c.attacks(), c.code());
        List<PokemonAbility> abilityList = mappingAbilityData(c.abilities(), c.code());

        return new PokemonCard(c, attackList, abilityList);
    }
}
