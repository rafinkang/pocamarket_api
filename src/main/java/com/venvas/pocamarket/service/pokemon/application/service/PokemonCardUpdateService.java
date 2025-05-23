package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCardDto;
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

    public List<PokemonCardDto> updateJsonData(String version) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/sample/" + version + ".json");
        TypeReference<List<PokemonCardDto>> typeReference = new TypeReference<>() {};

        try {
            List<PokemonCardDto> pokemonCards = mapper.readValue(inputStream, typeReference);
            log.info("pokemon Card : {}", pokemonCards);

            List<PokemonCard> pokemonCardList = new ArrayList<>();
//            pokemonCards.stream()
//                    .map(card -> {
//                        PokemonCard pokemonCard = new PokemonCard();
//                        pokemonCard.set
//                    })

            return pokemonCards;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
