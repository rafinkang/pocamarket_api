package com.venvas.pocamarket.service.pokemon.domain.exception.valid;

import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PokemonStrValidator implements ConstraintValidator<PokemonStrParam, String> {

    private String pattern;
    private PokemonErrorCode errorCode;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            addConstraintViolation(context, errorCode.getMessage() + " (null 값은 허용되지 않습니다)");
            return false;
        }

        if(!value.matches(pattern)) {
            addConstraintViolation(context, String.format("[%s] %s (입력값: %s)", errorCode.getCode(), errorCode.getMessage(), value));
            return false;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        // 기본 제공되는 메시지를 비활성화
        context.disableDefaultConstraintViolation();
        // 새로운 검증 오류 메시지를 생성하고 등록
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
