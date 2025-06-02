package com.venvas.pocamarket.service.pokemon.domain.value;

import java.util.List;

public final class CardRarity {
    private static final List<String> list = List.of(
            "COMMON", "UNCOMMON", "RARE", "RARE EX", "FULL ART", "FULL ART EX/SUPPORT", "IMMERSIVE", "GOLD CROWN"
    );

    public static List<String> getList() {
        return list;
    }
}
