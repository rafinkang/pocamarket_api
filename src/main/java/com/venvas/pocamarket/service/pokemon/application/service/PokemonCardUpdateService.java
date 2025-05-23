package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public List<PokemonCard> updateJsonData(String version) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/sample/" + version + ".json");
        TypeReference<List<PokemonCardDto>> typeReference = new TypeReference<>() {};

        try {
            List<PokemonCardDto> pokemonCards = mapper.readValue(inputStream, typeReference);
            log.info("pokemon Card : {}", pokemonCards);

            List<PokemonCard> cardList = new ArrayList<>();

            for (PokemonCardDto card : pokemonCards) {
                cardList.add(mappingCardData(card));
            }

            return pokemonCardRepository.saveAll(cardList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<PokemonAbility> mappingAbilityData(String code, List<AbilityDto> aDto) {
        if(aDto == null) return null;

        List<PokemonAbility> list = new ArrayList<>();
        for (AbilityDto a : aDto) {
            list.add(new PokemonAbility(
                null,
                code,
                a.getName(),
                a.getName_ko(),
                a.getEffect(),
                a.getEffect_ko(),
                null
            ));
        }
        return list;
    }

    private List<PokemonAttack> mappingAttackData(String code, List<AttacksDto> aDto) {
        if(aDto == null) return null;

        List<PokemonAttack> list = new ArrayList<>();
        for (AttacksDto a : aDto) {
            list.add(new PokemonAttack(
                    null,
                    code,
                    a.getName(),
                    a.getName_ko(),
                    a.getEffect(),
                    a.getEffect_ko(),
                    a.getDamage(),
                    String.join(",", a.getCost()),
                    null
            ));
        }
        return list;
    }

    private PokemonCard mappingCardData(PokemonCardDto c) {
        List<PokemonAttack> attackList = mappingAttackData(c.getCode(),c.getAttacks());
        List<PokemonAbility> abilityList = mappingAbilityData(c.getCode(),c.getAbilities());

        return new PokemonCard(
                null,
                c.getCode(),
                null,
                null,
                c.getName(),
                c.getName_ko(),
                c.getElement(),
                c.getType(),
                c.getSubtype(),
                c.getHealth(),
                c.getSet(),
                c.getPack(),
                c.getRetreatCost(),
                c.getWeakness(),
                c.getEvolvesFrom(),
                c.getRarity(),
                attackList,
                abilityList
        );
    }
}
