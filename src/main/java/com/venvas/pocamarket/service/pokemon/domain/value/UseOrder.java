package com.venvas.pocamarket.service.pokemon.domain.value;


import java.util.List;

public final class UseOrder {
    private static final List<String> list = List.of("code", "nameKo");

    public static List<String> getList() {
        return list;
    }
}