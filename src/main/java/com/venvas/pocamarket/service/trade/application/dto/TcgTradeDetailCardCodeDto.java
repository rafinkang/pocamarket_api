package com.venvas.pocamarket.service.trade.application.dto;

import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;

import lombok.Getter;

@Getter
public class TcgTradeDetailCardCodeDto {
    private final String code;
    private final TradeCardCodeStatus type;
    private final String name;
    private final String nameKo; 
    private final String rarity; 

    public TcgTradeDetailCardCodeDto(TcgTradeCardCode tcgTradecardCode, PokemonCard pokemonCard) {
        this.code = tcgTradecardCode.getCardCode();
        this.type = TradeCardCodeStatus.fromDbCode(tcgTradecardCode.getType());
        this.name = pokemonCard.getName();
        this.nameKo = pokemonCard.getNameKo();
        this.rarity = pokemonCard.getRarity();
    }
}
