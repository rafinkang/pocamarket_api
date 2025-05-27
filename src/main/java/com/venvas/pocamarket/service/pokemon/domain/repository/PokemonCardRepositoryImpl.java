package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.QPokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonCard.pokemonCard;

@Slf4j
public class PokemonCardRepositoryImpl implements PokemonCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final int MIN_PAGE_SIZE = 1;
    private final int MAX_PAGE_SIZE = 30;

    private final List<String> cardTypeList = List.of("POKEMON", "TRAINER");
    private final List<String> cardSubtypeList = List.of("BASIC", "STAGE_1", "STAGE_2", "ITEM", "SUPPORTER", "TOOL");
    private final List<String> packSetList = List.of("A1", "A", "A1a", "A2");
    private final List<String> orderList = List.of("code", "nameKo", "rarity");

    public PokemonCardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PokemonCardListDto> searchFilterList(PokemonCardListFilterSearchCondition condition, Pageable pageable) {

        long pageSize = checkMinMax(MIN_PAGE_SIZE, MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = Math.max(pageable.getOffset(), 0);

        // default 정렬
        if(pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("code")));
        }

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

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Page<List<PokemonCardListDto>> searchFilterSliceList(PokemonCardListFilterSearchCondition condition, Pageable pageable) {

        boolean hasNext = false;

        // pageable.getPageSize() < 불러온 페이지 개수

//        return new SliceImpl<>();
        return null;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        pageable.getSort().stream()
                .filter(sort -> orderList.stream().anyMatch(order -> order.equals(sort.getProperty())))
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

        return pokemonCard.rarity.in(splitAndTrim(rarity, ","));
    }

    private BooleanExpression packEq(String pack) {
        return textEmptyCheck(pack) ? pokemonCard.pack.eq(pack) : null;
    }

    private BooleanExpression packSetEq(String packSet) {
        if(! textEmptyCheck(packSet)) return null;

        return packSetList.stream().anyMatch(cardType -> cardType.equalsIgnoreCase(packSet)) ? pokemonCard.packSet.contains("(" + packSet + ")") : null;
    }

    private BooleanExpression subTypeEqIn(String subtype) {
        if(! textEmptyCheck(subtype)) return null;

        List<String> upSubTypeList = splitAndTrim(subtype, ",");

        // 목록에 서브 타입 있는지 확인
        List<String> subtypeList = upSubTypeList.stream().filter(cardSubtype -> cardSubtypeList.stream().anyMatch(sub -> sub.equalsIgnoreCase(cardSubtype))).toList();
        return !subtypeList.isEmpty() ? pokemonCard.subtype.in(subtypeList) : null;
    }

    private BooleanExpression typeEq(String type) {
        if(! textEmptyCheck(type)) return null;
        // 목록에 주 타입 있는지 확인
        return cardTypeList.stream().anyMatch(cardType -> cardType.equalsIgnoreCase(type)) ? pokemonCard.type.contains(type) : null;
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
