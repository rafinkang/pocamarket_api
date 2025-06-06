package com.venvas.pocamarket.service.pokemon.api.validator;

import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PokemonStrValidator implements ConstraintValidator<PokemonStrParam, String> {

    private String pattern;
    private PokemonErrorCode errorCode;

    @Override
    public void initialize(PokemonStrParam constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
        this.errorCode = constraintAnnotation.errorCode();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            addConstraintViolation(context, errorCode.getMessage() + " (null 값은 허용되지 않습니다)");
            return false;
        }

        if (!value.matches(pattern)) {
            addConstraintViolation(context, String.format("[%s] %s (입력값: %s)", 
                errorCode.getCode(), errorCode.getMessage(), value));
            return false;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}