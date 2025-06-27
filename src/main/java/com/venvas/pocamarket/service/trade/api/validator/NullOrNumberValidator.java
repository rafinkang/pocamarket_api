package com.venvas.pocamarket.service.trade.api.validator;

import jakarta.validation.ConstraintValidator;// Validator 구현
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NullOrNumberValidator implements ConstraintValidator<NullOrNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("Null Or number : {}", value);
        if (value == null) {
            log.info("들어와라");
            return true;
        }
        return value.matches("^[0-9]+$");
    }
}