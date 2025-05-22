package com.venvas.pocamarket.service.pokemon.domain.entity;

public enum CardType {
    POKEMON("Pokemon"),  // 포켓몬
    TRAINER("Trainer");  // 트레이너

    private String cardType;

    CardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return cardType;
    }
}
