package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.PokemonAbilityDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.PokemonAttackDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.*;
import com.venvas.pocamarket.service.pokemon.domain.entity.*;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonException;
import com.venvas.pocamarket.service.pokemon.domain.value.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonAbility.pokemonAbility;
import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonAttack.pokemonAttack;
import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonCard.pokemonCard;

@Slf4j
public class PokemonCardRepositoryImpl implements PokemonCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final int MIN_PAGE_SIZE = 1;
    private final int MAX_PAGE_SIZE = 30;


    public PokemonCardRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PokemonCard> findByPackSetLikeList(String packSet) {

        return queryFactory
                .from(pokemonCard)
                .leftJoin(pokemonAttack)
                .on(pokemonAttack.cardCode.eq(pokemonCard.code))
                .leftJoin(pokemonAbility)
                .on(pokemonAbility.cardCode.eq(pokemonCard.code))
                .where(packSetEq(packSet))
                .orderBy(pokemonCard.cardId.asc(),
                        pokemonAttack.attackId.asc(),
                        pokemonAbility.abilityId.asc())
                .transform(
                        GroupBy.groupBy(pokemonCard.code).as(
                                Projections.constructor(
                                        PokemonCard.class,
                                        pokemonCard.cardId,
                                        pokemonCard.code,
                                        pokemonCard.dexId,
                                        pokemonCard.dexGroup,
                                        pokemonCard.name,
                                        pokemonCard.nameKo,
                                        pokemonCard.element,
                                        pokemonCard.type,
                                        pokemonCard.subtype,
                                        pokemonCard.health,
                                        pokemonCard.packSet,
                                        pokemonCard.pack,
                                        pokemonCard.retreatCost,
                                        pokemonCard.weakness,
                                        pokemonCard.evolvesFrom,
                                        pokemonCard.rarity,
                                        GroupBy.list(
                                            Projections.constructor(
                                                PokemonAttack.class,
                                                pokemonAttack.attackId,
                                                pokemonAttack.cardCode,
                                                pokemonAttack.name,
                                                pokemonAttack.nameKo,
                                                pokemonAttack.effect,
                                                pokemonAttack.effectKo,
                                                pokemonAttack.damage,
                                                pokemonAttack.cost
                                        )),
                                        GroupBy.list(Projections.constructor(
                                                PokemonAbility.class,
                                                pokemonAbility.abilityId,
                                                pokemonAbility.cardCode,
                                                pokemonAbility.name,
                                                pokemonAbility.nameKo,
                                                pokemonAbility.effect,
                                                pokemonAbility.effectKo
                                        ))
                                )
                        )
                ).values().stream().toList();
    }

    @Override
    public Optional<PokemonCardDetailDto> findByCodeDetailCard(String code) {
        return Optional.ofNullable(queryFactory
                .from(pokemonCard)
                .leftJoin(pokemonAttack)
                    .on(pokemonAttack.cardCode.eq(pokemonCard.code))
                .leftJoin(pokemonAbility)
                    .on(pokemonAbility.cardCode.eq(pokemonCard.code))
                .where(codeEq(code))
                .transform(
                    GroupBy.groupBy(pokemonCard.code).as(
                        Projections.constructor(
                            PokemonCardDetailDto.class,
                            pokemonCard.code,
                            pokemonCard.nameKo,
                            pokemonCard.element,
                            pokemonCard.type,
                            pokemonCard.subtype,
                            pokemonCard.health,
                            pokemonCard.packSet,
                            pokemonCard.pack,
                            pokemonCard.retreatCost,
                            pokemonCard.weakness,
                            pokemonCard.evolvesFrom,
                            pokemonCard.rarity,
                            GroupBy.list(Projections.constructor(
                                    PokemonAttackDetailDto.class,
                                    pokemonAttack.nameKo,
                                    pokemonAttack.effectKo,
                                    pokemonAttack.damage,
                                    pokemonAttack.cost
                            )),
                            GroupBy.list(Projections.constructor(
                                    PokemonAbilityDetailDto.class,
                                    pokemonAbility.nameKo,
                                    pokemonAbility.effectKo
                            ))
                        )
                    )
                )
                .get(code));
    }

    @Override
    public Page<PokemonCardListDto> searchFilterList(PokemonCardListFilterSearchCondition condition, Pageable pageable) {

        long pageSize = checkMinMax(MIN_PAGE_SIZE, MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = Math.max(pageable.getOffset(), 0);

        // default 정렬
        if(pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("code")));
        }

        List<PokemonCardListDto> content = getPokemonCardListQuery(condition)
                .orderBy(getOrderSpecifier(pageable))
                .offset(offset)
                .limit(pageSize)
                .fetch();

        JPAQuery<Long> countQuery = getCountQuery(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * count 쿼리를 안날림, 다음 페이지가 있는지 없는지만 확인.
     */
    public Slice<PokemonCardListDto> searchFilterSliceList(PokemonCardListFilterSearchCondition condition, Pageable pageable) {

        long pageSize = checkMinMax(MIN_PAGE_SIZE, MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = Math.max(pageable.getOffset(), 0);

        List<PokemonCardListDto> content = getPokemonCardListQuery(condition)
                .orderBy(getOrderSpecifier(pageable))
                .offset(offset)
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;

        if(content.size() > pageSize) {
            content.remove((int) pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private JPAQuery<Long> getCountQuery(PokemonCardListFilterSearchCondition condition) {
        return queryFactory
                .select(pokemonCard.count())
                .from(pokemonCard)
                .where(
                        nameLike(condition.getNameKo()),
                        typeAndSubTypeLike(condition.getType(), condition.getSubtype()),
                        elementEqIn(condition.getElement()),
                        packSetEq(condition.getPackSet()),
                        packEq(condition.getPack()),
                        rarityEqIn(condition.getRarity())
                );
    }

    private JPAQuery<PokemonCardListDto> getPokemonCardListQuery(PokemonCardListFilterSearchCondition condition) {
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
                        nameLike(condition.getNameKo()),
                        typeAndSubTypeLike(condition.getType(), condition.getSubtype()),
                        elementEqIn(condition.getElement()),
                        packSetEq(condition.getPackSet()),
                        packEq(condition.getPack()),
                        rarityEqIn(condition.getRarity())
                );
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        pageable.getSort().stream()
                .filter(sort -> UseOrder.getList().stream().anyMatch(order -> order.equals(sort.getProperty())))
                .forEach(sort -> {
                    // 오름,내림차순
                    Order order = sort.isAscending() ? Order.ASC : Order.DESC;
                    // 프로퍼티값
                    String property = sort.getProperty();
                    // queryDsl의 컬렴명을 동적으로 Path 객체로 만들어주는 유틸
                    PathBuilder<PokemonCard> pathBuilder = new PathBuilder<>(pokemonCard.getType(), pokemonCard.getMetadata());
                    OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, pathBuilder.getString(property));

                    orders.add(orderSpecifier);
                });

        return orders.toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression rarityEqIn(String rarity) {
        if(! textEmptyCheck(rarity)) return null;

        List<String> rarityList = splitAndTrim(rarity, ",");

        if(!validateAllValuesInList(rarityList, CardRarity.getList())) {
            throw new PokemonException(PokemonErrorCode.INVALID_SEARCH_VALUE, "잘못된 레어도 값 입니다. value = " + rarity);
        }

        return pokemonCard.rarity.in(rarityList);
    }

    private BooleanExpression packEq(String pack) {
        return textEmptyCheck(pack) ? pokemonCard.pack.eq(pack) : null;
    }

    private BooleanExpression packSetEq(String packSet) {
        if(! textEmptyCheck(packSet)) return null;

        boolean result = CardPackSet.getList().stream().anyMatch(cardType -> cardType.equalsIgnoreCase(packSet));

        if(!result) {
            throw new PokemonException(PokemonErrorCode.INVALID_SEARCH_VALUE, "잘못된 확장팩 값 입니다. value = " + packSet);
        }

        return pokemonCard.packSet.contains("(" + packSet + ")");
    }

    private BooleanExpression subTypeEqIn(String subtype) {
        if(! textEmptyCheck(subtype)) return null;

        List<String> subtypeList = splitAndTrim(subtype, ",");

        if(!validateAllValuesInList(subtypeList, CardSubType.getList())) {
            throw new PokemonException(PokemonErrorCode.INVALID_SEARCH_VALUE, "잘못된 서브타입 값 입니다. value = " + subtype);
        }

        return pokemonCard.subtype.in(subtypeList);
    }

    private BooleanExpression typeEq(String type) {
        if(! textEmptyCheck(type)) return null;

        boolean result = CardType.getList().stream().anyMatch(cardType -> cardType.equalsIgnoreCase(type));

        if(!result) {
            throw new PokemonException(PokemonErrorCode.INVALID_SEARCH_VALUE, "잘못된 타입 값 입니다. value = " + type);
        }

        return pokemonCard.type.contains(type);
    }

    private BooleanBuilder typeAndSubTypeLike(String type, String subType) {
        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression typeEqExpression = typeEq(type);
        builder.and(typeEqExpression);
        if(typeEqExpression != null) {
            builder.and(subTypeEqIn(subType));
        }
        return builder;
    }

    private BooleanExpression nameLike(String nameKo) {
        return textEmptyCheck(nameKo) ? pokemonCard.nameKo.contains(nameKo) : null; // %str%
    }

    private BooleanExpression codeEq(String code) {
        return textEmptyCheck(code) ? pokemonCard.code.eq(code) : null;
    }

    private BooleanExpression elementEqIn(String element) {
        if(! textEmptyCheck(element)) return null;
        List<String> elementList = splitAndTrim(element, ",");

        if(!validateAllValuesInList(elementList, CardElement.getList())) {
            throw new PokemonException(PokemonErrorCode.INVALID_SEARCH_VALUE, "잘못된 속성 값 입니다. value = " + element);
        }

        return pokemonCard.element.in(elementList);
    }

    private boolean validateAllValuesInList(List<String> inputList, List<String> allowedValues) {
        return inputList.stream()
                .allMatch(input -> allowedValues.stream()
                        .anyMatch(allowed ->
                                allowed.equalsIgnoreCase(input)));
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
