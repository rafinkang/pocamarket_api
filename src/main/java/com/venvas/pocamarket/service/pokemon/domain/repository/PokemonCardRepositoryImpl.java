package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.pokemon.application.dto.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.PokeCardSearchListFilterCondition;
import com.venvas.pocamarket.service.pokemon.application.dto.QPokemonCardListDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonCard.pokemonCard;

@Slf4j
public class PokemonCardRepositoryImpl implements PokemonCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PokemonCardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<PokemonCardListDto> searchFilterList(PokeCardSearchListFilterCondition condition) {
        return queryFactory
                .select(new QPokemonCardListDto(
                        pokemonCard.code,
                        pokemonCard.nameKo,
                        pokemonCard.element,
                        pokemonCard.type,
                        pokemonCard.subtype,
                        pokemonCard.packSet,
                        pokemonCard.pack,
                        pokemonCard.rarity
                ))
                .from(pokemonCard)
                .where(
                        // 조건 추가해야함
                        nameLike(condition.getNameKo()),
                        elementEq(condition.getElement())
                )
                .fetch();
    }

    private BooleanExpression nameLike(String nameKo) {
        return textEmptyCheck(nameKo) ? pokemonCard.nameKo.contains(nameKo) : null; // %str%
    }

    private BooleanExpression elementEq(String element) {
        if(! textEmptyCheck(element)) return null;

        return pokemonCard.element.in(splitAndTrim(element, ","));
    }

    private boolean textEmptyCheck(String text) {
        return StringUtils.hasText(text) && StringUtils.hasLength(text);
    }

    private List<String> splitAndTrim(String str, String regex) {
        return Arrays.stream(str.split(regex))
                .map(String::trim)
                .toList();
    }
}
