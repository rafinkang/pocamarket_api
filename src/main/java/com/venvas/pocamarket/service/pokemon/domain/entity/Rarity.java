package com.venvas.pocamarket.pokemon.domain;

public enum Rarity {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    RARE_EX("Rare Ex"),
    FULL_ART("Full Art"),
    FULL_ART_EX_SUPPORT("Full Art Ex/Support"),
    IMMERSIVE("Immersive"),
    GOLD_CROWN("Gold Crown");

    private String rarity;

    Rarity(String rarity) {
        this.rarity = rarity;
    }

    public String getRarity() {
        return rarity;
    }
}
