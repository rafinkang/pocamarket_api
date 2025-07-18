package com.venvas.pocamarket.service.pokemon.api.validator;

import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PokemonStrValidator.class)
public @interface PokemonStrParam {
    String message() default "Invalid Pokemon string parameter";
    PokemonErrorCode errorCode();
    String pattern();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}