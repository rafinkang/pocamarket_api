package com.venvas.pocamarket.service.trade.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeRequestGetResponse;
import com.venvas.pocamarket.service.trade.domain.enums.TcgTradeRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.venvas.pocamarket.service.pokemon.domain.entity.QPokemonCard.pokemonCard;
import static com.venvas.pocamarket.service.trade.domain.entity.QTcgTradeRequest.tcgTradeRequest;
import static com.venvas.pocamarket.service.trade.domain.entity.QTcgTradeUser.tcgTradeUser;

@RequiredArgsConstructor
public class TcgTradeRequestRepositoryImpl implements TcgTradeRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TcgTradeRequestGetResponse> findTradeRequestsWithTradeUser(Long tradeId, String userUuid, Boolean isAdmin) {
        
        List<TcgTradeRequestGetResponse> result = queryFactory
            .select(Projections.constructor(TcgTradeRequestGetResponse.class,
                tcgTradeRequest.id,
                tcgTradeRequest.trade.id,
                tcgTradeRequest.nickname,
                tcgTradeRequest.requestCardCode,
                tcgTradeRequest.status,
                tcgTradeRequest.uuid,
                tcgTradeRequest.updatedAt,
                tcgTradeUser.tradeCount.coalesce(0),
                tcgTradeUser.reportCount.coalesce(0),
                tcgTradeUser.exp.coalesce(0),
                pokemonCard.nameKo.coalesce(""),
                pokemonCard.element.coalesce(""),
                pokemonCard.packSet.coalesce(""),
                pokemonCard.rarity.coalesce("")
            ))
            .from(tcgTradeRequest)
            .leftJoin(tcgTradeUser)
                .on(tcgTradeRequest.uuid.eq(tcgTradeUser.uuid))
            .leftJoin(pokemonCard)
                .on(tcgTradeRequest.requestCardCode.eq(pokemonCard.code))
            .where(
                tcgTradeRequest.trade.id.eq(tradeId),
                statusCondition(isAdmin)
            )
            .orderBy(
                tcgTradeRequest.status.desc(),
                tcgTradeRequest.updatedAt.desc()
            )
            .fetch();

        result.forEach(response -> response.setMyFlag(userUuid));
        return result;
    }

    private BooleanExpression statusCondition(Boolean isAdmin) {
        if (isAdmin != null && isAdmin) {
            return null; // 관리자는 모든 상태 조회
        }
        return tcgTradeRequest.status.ne(TcgTradeRequestStatus.DELETE.getCode()); // 일반 사용자는 삭제되지 않은 것만
    }
} 