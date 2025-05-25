package com.venvas.pocamarket.service.pokemon.domain.enums;

public enum CardSubType {
    BASIC("Basic"),
    STAGE_1("Stage 1"),
    STAGE_2("Stage 2"),
    SUPPORTER("Supporter"),  // 서포터
    ITEM("Item");       // 아이템

    private String cardSubType;

    CardSubType(String subType) {
        this.cardSubType = subType;
    }

    public String getCardSubType() {
        return cardSubType;
    }
    
}
