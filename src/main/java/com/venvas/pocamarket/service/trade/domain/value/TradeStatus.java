package com.venvas.pocamarket.service.trade.domain.value;

import java.util.List;


public class TradeStatus {
    private static final List<String> list = List.of(
        "all",          // 전부 다
        "request",      // 신청한 교환
        "progress",     // 진행중인 교환
        "complete"      // 완료된 교환
    );
    private static final List<String> myList = List.of(
        "my-all",       // 내 교환 전체 보기
        "my-request",   // 내가 신청한 교환
        "my-progress",  // 내 진행 중인 교환
        "my-complete"   // 내 완료된 교환
    );

    public static List<String> getList() {
        return list;
    }

    public static List<String> getMyList() {
        return myList;
    }

    public static int convertStatus(String status) {
        return switch (status) {
            case "all", "my-all" -> 98;
            case "request", "my-request" -> 1;
            case "progress", "my-progress" -> 2;
            case "complete", "my-complete" -> 3;
            default -> 99;
        };
    }
}
