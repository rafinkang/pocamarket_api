package com.venvas.pocamarket.service.pokemon.domain.value;

import java.util.List;

public final class CardPackSet {
    private static final List<String> list = List.of("A1", "A", "A1a", "A2", "T9"); //T9 테스트용

    public static List<String> getList() {
        return list;
    }
}