package com.venvas.pocamarket.service.pokemon.domain.value;

import java.util.List;

public final class CardElement {
    private static final List<String> list = List.of(
            "GRASS", "FIRE", "WATER", "LIGHTNING", "PSYCHIC", "FIGHTING",
            "DARKNESS", "METAL", "DRAGON", "COLORLESS"
    );

    public static List<String> getList() {
        return list;
    }
}