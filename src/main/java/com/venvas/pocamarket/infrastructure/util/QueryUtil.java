package com.venvas.pocamarket.infrastructure.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;

public final class QueryUtil {

    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 30;

    /**
     * @param min 최소 limit
     * @param max 최대 limit
     * @param v 비교 limit
     * @return 최소, 최대 비교 후 출력
     */
    public static long checkMinMax(long min, long max, long v) {
        long m = Math.max(v, min); // 최소값 보다 큰 값 리턴
        return Math.min(m, max); // 최대값 리턴
    }

    /**
     * 0보다 큰 offset값 리턴
     * @return
     */
    public static long checkOffsetMax(long offset) {
        return Math.max(offset, 0);
    }

    /**
     * Pageable 객체의 정렬 정보를 QueryDSL OrderSpecifier 배열로 변환
     * @param pageable 페이징 정보
     * @param queryDslEntity QueryDSL 엔티티 경로
     * @param filterList 허용된 정렬 필드 목록
     * @return OrderSpecifier 배열
     * @param <T> 엔티티 타입
     */
    public static <T> OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, EntityPathBase<T> queryDslEntity, List<String> filterList) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        pageable.getSort().stream()
                .filter(sort -> filterList.stream().anyMatch(order -> order.equals(sort.getProperty())))
                .forEach(sort -> {
                    // 오름,내림차순
                    Order order = sort.isAscending() ? Order.ASC : Order.DESC;
                    // 프로퍼티값
                    String property = sort.getProperty();
                    // queryDsl의 컬럼명을 동적으로 Path 객체로 만들어주는 유틸
                    PathBuilder<T> pathBuilder = new PathBuilder<>(queryDslEntity.getType(), queryDslEntity.getMetadata().getName());
                    OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, pathBuilder.getString(property));

                    orders.add(orderSpecifier);
                });

        return orders.toArray(OrderSpecifier[]::new);
    }
}
