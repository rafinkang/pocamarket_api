package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokeCardSearchListFilterCondition;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.QPokemonCardListDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonCard.pokemonCard;

@Slf4j
public class PokemonCardRepositoryImpl implements PokemonCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final int MAX_PAGE_SIZE = 30;

    private final List<String> cardTypeList = List.of("POKEMON", "TRAINER");
    private final List<String> cardSubtypeList = List.of("BASIC", "STAGE_1", "STAGE_2", "ITEM", "SUPPORTER", "TOOL");
    private final List<String> packSetList = List.of("A1", "A", "A1a", "A2");

    public PokemonCardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<List<PokemonCardListDto>> searchFilterList(PokeCardSearchListFilterCondition condition, Pageable pageable) {

        long pageSize = checkMinMax(0, MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = Math.max(pageable.getOffset(), 0);

        log.info("sorting info = {}", pageable.getSort());

        List<PokemonCardListDto> content = queryFactory
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
                        nameLike(condition.getNameKo()),
                        typeAndSubTypeLike(condition.getType(), condition.getSubType()),
                        elementEqIn(condition.getElement()),
                        packSetEq(condition.getPackSet()),
                        packEq(condition.getPack()),
                        rarityEqIn(condition.getRarity())
                )
                .orderBy(getOrderSpecifier(pageable))
                .offset(offset)
                .limit(pageSize)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(pokemonCard.count())
                .from(pokemonCard)
                .where(
                        nameLike(condition.getNameKo()),
                        typeAndSubTypeLike(condition.getType(), condition.getSubType()),
                        elementEqIn(condition.getElement()),
                        packSetEq(condition.getPackSet()),
                        packEq(condition.getPack()),
                        rarityEqIn(condition.getRarity())
                );

        return PageableExecutionUtils.getPage(Collections.singletonList(content), pageable, countQuery::fetchOne);
    }

    public Page<List<PokemonCardListDto>> searchFilterSliceList(PokeCardSearchListFilterCondition condition, Pageable pageable) {

        boolean hasNext = false;

        // pageable.getPageSize() < 불러온 페이지 개수

//        return new SliceImpl<>();
        return null;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        pageable.getSort().stream()
                .forEach(sort -> {
                    // 오름,내림차순
                    Order order = sort.isAscending() ? Order.ASC : Order.DESC;
                    // 프로퍼티값
                    String property = sort.getProperty();
                    // queryDsl의 컬렴명을 동적으로 Path 객체로 만들어주는 유틸 ex) pokemonCard.code(property 값)
                    Path<Object> target = Expressions.path(Object.class, pokemonCard, property);
                    OrderSpecifier<?> orderSpecifier = new OrderSpecifier(order, target);
                    orders.add(orderSpecifier);
                });
        return orders.toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression rarityEqIn(String rarity) {
        if(! textEmptyCheck(rarity)) return null;

        return pokemonCard.rarity.in(splitAndTrim(rarity, ","));
    }

    private BooleanExpression packEq(String pack) {
        return textEmptyCheck(pack) ? pokemonCard.pack.contains(pack) : null;
    }

    private BooleanExpression packSetEq(String packSet) {
        if(! textEmptyCheck(packSet)) return null;
        String upStr = packSet.toUpperCase();

        return packSetList.stream().anyMatch(cardType -> cardType.equals(upStr)) ? pokemonCard.subtype.contains("(" + upStr + ")") : null;
    }

    private BooleanExpression subTypeEq(String subtype) {
        if(! textEmptyCheck(subtype)) return null;
        String upType = subtype.toUpperCase();

        // 목록에 서브 타입 있는지 확인
        return cardSubtypeList.stream().anyMatch(cardType -> cardType.equals(upType)) ? pokemonCard.subtype.contains(upType) : null;
    }

    private BooleanExpression typeEq(String type) {
        if(! textEmptyCheck(type)) return null;
        String upType = type.toUpperCase();

        // 목록에 주 타입 있는지 확인
        return cardTypeList.stream().anyMatch(cardType -> cardType.equals(upType)) ? pokemonCard.type.contains(upType) : null;
    }

    private BooleanExpression typeAndSubTypeLike(String type, String subType) {
        BooleanExpression typeBoolean = typeEq(type);

        if(typeBoolean == null) return null;

        BooleanExpression subtypeBoolean = subTypeEq(type);

        if(subtypeBoolean != null) typeBoolean.and(subtypeBoolean);

        return typeBoolean;
    }

    private BooleanExpression nameLike(String nameKo) {
        return textEmptyCheck(nameKo) ? pokemonCard.nameKo.contains(nameKo) : null; // %str%
    }

    private BooleanExpression elementEqIn(String element) {
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

    /**
     * @param min 최소값
     * @param max 최대값
     */
    private long checkMinMax(long min, long max, long v) {
        long m = Math.max(v, min); // 최소값 보다 큰 값 리턴
        return Math.min(m, max); // 최대값 리턴
    }
}
