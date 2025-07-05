package com.venvas.pocamarket.service.trade.application.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.PokemonAbilityDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.PokemonAttackDetailDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAbility;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAttack;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.trade.domain.entity.TcgTradeCardCode;
import com.venvas.pocamarket.service.trade.domain.enums.TradeCardCodeStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
public class TcgTradeDetailCardCodeDto {
    private final String code;
    private final TradeCardCodeStatus type;
    private final String name;
    private final String nameKo; 
    private final String rarity; 
    private final String element;
    private final Integer health;
    private final String packSet;
    private final String pack;
    private final Integer retreatCost;
    private final String weakness;
    private final List<PokemonAttackDetailDto> attackList;
    private final List<PokemonAbilityDetailDto> abilityList;

    public TcgTradeDetailCardCodeDto(TcgTradeCardCode tcgTradecardCode, PokemonCard pokemonCard) {
        this.code = tcgTradecardCode.getCardCode();
        this.type = TradeCardCodeStatus.fromDbCode(tcgTradecardCode.getType());
        this.name = pokemonCard.getName();
        this.nameKo = pokemonCard.getNameKo();
        this.rarity = pokemonCard.getRarity();
        this.element = pokemonCard.getElement();
        this.health = pokemonCard.getHealth();
        this.packSet = pokemonCard.getPackSet();
        this.pack = pokemonCard.getPack();
        this.retreatCost = pokemonCard.getRetreatCost();
        this.weakness = pokemonCard.getWeakness();

        List<PokemonAttack> attacks = pokemonCard.getAttacks();
        List<PokemonAbility> abilities = pokemonCard.getAbilities();

        List<PokemonAttackDetailDto> attackList = attacks.stream()
            .map(attack -> new PokemonAttackDetailDto(
                attack.getNameKo(),
                attack.getEffectKo(),
                attack.getDamage(),
                attack.getCost()
            ))
            .collect(Collectors.toList());
        List<PokemonAbilityDetailDto> abilityList = abilities.stream()
            .map(attack -> new PokemonAbilityDetailDto(
                attack.getNameKo(),
                attack.getEffectKo()
            ))
            .collect(Collectors.toList());
        
        // attackList에서 nameKo가 null인 항목들을 제거
        this.attackList = attackList != null ? 
            attackList.stream()
                .filter(attack -> attack.getNameKo() != null)
                .toList() : 
            List.of();
        // abilityList에서 nameKo와 effectKo가 모두 null인 항목들을 제거
        this.abilityList = abilityList != null ? 
            abilityList.stream()
                .filter(ability -> ability.getNameKo() != null || ability.getEffectKo() != null)
                .toList() : 
            List.of();
    }
}
