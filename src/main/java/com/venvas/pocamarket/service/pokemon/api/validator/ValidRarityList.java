package com.venvas.pocamarket.service.pokemon.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RarityListValidator.class)
public @interface ValidRarityList {
    String message() default "유효하지 않은 레어도입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
