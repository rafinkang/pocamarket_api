package com.venvas.pocamarket.service.pokemon.domain.value;

import java.util.List;

public final class CardType {
    private static final List<String> list = List.of("POKEMON", "TRAINER");

    public static List<String> getList() {
        return list;
    }
}
