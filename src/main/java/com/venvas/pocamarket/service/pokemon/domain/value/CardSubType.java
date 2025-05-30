package com.venvas.pocamarket.service.pokemon.domain.value;


import java.util.List;

public final class CardSubType {
    private static final List<String> list = List.of("BASIC", "STAGE_1", "STAGE_2", "ITEM", "SUPPORTER", "TOOL");

    public static List<String> getList() {
        return list;
    }
}
