package com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PokemonCardListFilterSearchCondition {

    @Size(max = 30, message = "이름은 30자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]*$", message = "이름은 한글, 영문, 숫자만 입력 가능합니다.")
    private String nameKo;

    @Size(max = 10, message = "메인 타입은 10자를 초과할 수 없습니다.")
    @Pattern(regexp = "^(?i)(pokemon|trainer)$", message = "유효하지 않은 카드 타입입니다.")
    private String type;

    @Size(max = 15, message = "서브타입은 15자를 초과할 수 없습니다.")
    @Pattern(regexp = "^(?i)(basic|stage_1|stage_2|supporter|item|tool)$",
            message = "유효하지 않은 카드 서브타입입니다.")
    private String subtype;

    @Size(max = 50, message = "속성은 50자를 초과할 수 없습니다.")
    @Pattern(regexp = "^(?i)(grass|fire|water|lightning|psychic|fighting|darkness|metal|dragon|normal|colorless)$",
            message = "유효하지 않은 속성입니다.")
    private String element;

    @Size(max = 30, message = "확장팩은 30자를 초과할 수 없습니다.")
    private String packSet;

    @Size(max = 30, message = "팩은 30자를 초과할 수 없습니다.")
    private String pack;

    @Size(max = 30, message = "레어도는 30자를 초과할 수 없습니다.")
    @Pattern(regexp = "^(?i)(common|uncommon|rare|rare ex|full art|full art ex/support|immersive|gold crown)$",
            message = "유효하지 않은 레어도입니다.")
    private String rarity;
}
