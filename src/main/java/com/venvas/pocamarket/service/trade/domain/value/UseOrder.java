package com.venvas.pocamarket.service.trade.domain.value;

import java.util.List;

public final class UseOrder {
    private static final List<String> list = List.of("id", "createdAt", "updatedAt");

    public static List<String> getList() {
        return list;
    }
}