package com.venvas.pocamarket.service.pokemon.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PokemonStrArrValidator.class)
public @interface PokemonStrArrParam {
    String message() default "유효하지 않은 데이터입니다.";
    String expectedPokemonListPath() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
