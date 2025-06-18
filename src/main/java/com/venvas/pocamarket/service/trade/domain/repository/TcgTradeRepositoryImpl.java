package com.venvas.pocamarket.service.trade.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.infrastructure.util.QueryUtil;
import com.venvas.pocamarket.service.trade.application.dto.QTcgTradeListResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListResponse;
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


@Slf4j
public class TcgTradeRepositoryImpl implements TcgTradeRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private boolean isAdmin = false;
    private boolean isMy = false;

    public TcgTradeRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<TcgTradeListResponse> searchFilterList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin) {

        this.isAdmin = isAdmin;
        this.isMy = myCheck(request.getFilterOption());

        long pageSize = QueryUtil.checkMinMax(QueryUtil.MIN_PAGE_SIZE, QueryUtil.MAX_PAGE_SIZE, pageable.getPageSize());
        long offset = QueryUtil.checkOffsetMax(pageable.getOffset());

        // default 정렬
        if(pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("id")));
        }

        List<TcgTradeListResponse> content = getTradeListQuery(request, userUuid)
                .orderBy(QueryUtil.getOrderSpecifier(pageable, tcgTrade, UseOrder.getList()))
                .offset(offset)
                .limit(pageSize)
                .fetch();

        JPAQuery<Long> countQuery = getCountQuery(request, userUuid);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> getCountQuery(TcgTradeListRequest request, String userUuid) {
        return queryFactory
                .select(tcgTrade.count())
                .from(tcgTrade)
                .where(
                    statusEq(TradeStatus.convertStatus(request.getFilterOption())),
                    myCardEq(userUuid)
                );
    }

    private JPAQuery<TcgTradeListResponse> getTradeListQuery(TcgTradeListRequest request, String userUuid) {
        // TODO :: tcg_trade_card_code join, where에 card_code, type 검사

        return queryFactory
                .select(new QTcgTradeListResponse(
                        tcgTrade.id,
                        tcgTrade.nickname,
                        tcgTrade.status,
                        tcgTrade.createdAt
                ))
                .from(tcgTrade)
                .where(
                    statusEq(TradeStatus.convertStatus(request.getFilterOption())),
                    myCardEq(userUuid)
                );
    }

    private BooleanExpression statusEq(int status) {
        log.info("status 체크 : {}", status);
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
        if(this.isMy) return null;  // status 조건이 my가 아니면 return

        String uuid = userUuid.trim();

        if(uuid.isBlank()) return null; // 비어있는 경우 return

        return tcgTrade.uuid.eq(userUuid);
    }

    private boolean myCheck(String filerOption) {
        return TradeStatus.getList().stream().anyMatch(f -> f.equals(filerOption));
    }
}
