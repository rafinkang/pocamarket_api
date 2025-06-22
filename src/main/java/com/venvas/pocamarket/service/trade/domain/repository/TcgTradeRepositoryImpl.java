package com.venvas.pocamarket.service.trade.domain.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.infrastructure.util.QueryUtil;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeCardCodeDto;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListDto;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgTradeException;
import com.venvas.pocamarket.service.trade.domain.value.TradeStatus;
import com.venvas.pocamarket.service.trade.domain.value.UseOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.venvas.pocamarket.service.trade.domain.entity.QTcgTrade.tcgTrade;
import static com.venvas.pocamarket.service.trade.domain.entity.QTcgTradeCardCode.tcgTradeCardCode;


@Slf4j
public class TcgTradeRepositoryImpl implements TcgTradeRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private boolean isAdmin = false;
    private boolean isMy = false;

    public TcgTradeRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<TcgTradeListDto> searchFilterList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.isMy = myCheck(request.getFilterOption(), userUuid);

        // default 정렬
        if(pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("id")));
        }

        List<TcgTradeListDto> content = getTradeListQuery(request, userUuid, pageable);

        JPAQuery<Long> countQuery = getCountQuery(request, userUuid);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> getCountQuery(TcgTradeListRequest request, String userUuid) {
        return queryFactory
            .select(tcgTrade.count())
            .from(tcgTrade)
            .where(
                statusEq(TradeStatus.convertStatus(request.getFilterOption())),
                myCardEq(userUuid),
                hasMatchingCards(request.getMyCardCode(), request.getWantCardCode())
        );
    }

    private List<TcgTradeListDto> getTradeListQuery(TcgTradeListRequest request, String userUuid, Pageable pageable) {
        long pageSize = QueryUtil.checkMinMax(QueryUtil.MIN_PAGE_SIZE, QueryUtil.MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = QueryUtil.checkOffsetMax(pageable.getOffset());
        // 조건에 맞는 거래 ID를 먼저 찾습니다
        List<Long> fetch = queryFactory
                .select(tcgTrade.id)
                .from(tcgTrade)
                .where(
                    statusEq(TradeStatus.convertStatus(request.getFilterOption())),
                    myCardEq(userUuid),
                    hasMatchingCards(request.getMyCardCode(), request.getWantCardCode())
                )
                .orderBy(QueryUtil.getOrderSpecifier(pageable, tcgTrade, UseOrder.getList()))
                .offset(offset)
                .limit(pageSize)
                .fetch();

        // 찾은 ID에 해당하는 모든 거래와 카드 정보를 조회합니다
        return queryFactory
                .from(tcgTrade)
                .leftJoin(tcgTradeCardCode)
                .on(tcgTradeCardCode.trade.id.eq(tcgTrade.id))
                .where(tcgTrade.id.in(fetch))
                .orderBy(QueryUtil.getOrderSpecifier(pageable, tcgTrade, UseOrder.getList()))
                .transform(GroupBy.groupBy(tcgTrade.id)
                    .list(Projections.constructor(
                        TcgTradeListDto.class,
                        tcgTrade.id,
                        tcgTrade.nickname,
                        tcgTrade.status,
                        tcgTrade.updatedAt,
                        tcgTrade.uuid,
                        GroupBy.list(Projections.constructor(
                            TcgTradeCardCodeDto.class,
                            tcgTradeCardCode.cardCode,
                            tcgTradeCardCode.type
                        ))
                    ))
                );
    }

    private BooleanExpression statusEq(int status) {
        if(isAdmin && status == 98) { // 관리자가 전체 검색
            return null;
        } else if(status == 98) { // 삭제 된 글 제외하고 보기
            return tcgTrade.status.ne(0);
        }

        if(status == 99) {
            throw new TcgTradeException(TcgTradeErrorCode.INVALID_SEARCH_STATUS, "잘못된 상태 값 입니다.");
        }

        return tcgTrade.status.eq(status);
    }

    private BooleanExpression myCardEq(String userUuid) {
        if(!this.isMy || userUuid == null) return null;

        String uuid = userUuid.trim();
        if(uuid.isBlank()) return null; // 비어있는 경우 return

        return tcgTrade.uuid.eq(uuid);
    }

    /**
     * 카드 코드 조건을 체크하는 메서드
     * 내 카드와 원하는 카드 조건을 OR 조건으로 연결
     */
    private BooleanExpression hasMatchingCards(String myCardCode, List<String> wantCardCodes) {
        BooleanExpression myCardCondition = null;
        BooleanExpression wantCardCondition = null;

        // 내 카드 조건 체크
        if (myCardCode != null && !myCardCode.isBlank()) {
            myCardCondition = JPAExpressions
                .selectOne()
                .from(tcgTradeCardCode)
                .where(
                    tcgTradeCardCode.trade.id.eq(tcgTrade.id),
                    tcgTradeCardCode.cardCode.eq(myCardCode),
                    tcgTradeCardCode.type.eq(1)
                )
                .exists();
        }

        // 원하는 카드 조건 체크
        if (wantCardCodes != null && !wantCardCodes.isEmpty()) {
            wantCardCondition = JPAExpressions
                .selectOne()
                .from(tcgTradeCardCode)
                .where(
                    tcgTradeCardCode.trade.id.eq(tcgTrade.id),
                    tcgTradeCardCode.cardCode.in(wantCardCodes),
                    tcgTradeCardCode.type.eq(2)
                )
                .exists();
        }

        // 둘 다 null이면 조건 없음
        if (myCardCondition == null && wantCardCondition == null) {
            return null;
        }

        // 둘 중 하나만 있으면 해당 조건만 반환
        if (myCardCondition == null) {
            return wantCardCondition;
        }
        if (wantCardCondition == null) {
            return myCardCondition;
        }

        // 둘 다 있으면 OR 조건으로 연결
        return myCardCondition.or(wantCardCondition);
    }

    /**
     * 필터 옵션 및 유저 체크
     */
    private boolean myCheck(String filterOption, String userUuid) {
        if(userUuid == null) {
            if(TradeStatus.getList().stream().noneMatch(f -> f.equals(filterOption))) {
                throw new TcgTradeException(TcgTradeErrorCode.INVALID_SEARCH_STATUS, "잘못된 상태 값입니다.");
            }
        } else {
            if(TradeStatus.getList().stream().anyMatch(f -> f.equals(filterOption)) ||
                TradeStatus.getMyList().stream().anyMatch(f -> f.equals(filterOption))) {
                return true;
            } else {
                throw new TcgTradeException(TcgTradeErrorCode.INVALID_SEARCH_STATUS, "잘못된 상태 값입니다.");
            }
        }
        return false;
    }
}
