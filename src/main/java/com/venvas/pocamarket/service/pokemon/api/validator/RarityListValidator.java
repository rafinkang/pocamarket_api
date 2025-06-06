package com.venvas.pocamarket.service.pokemon.api.validator;

import com.venvas.pocamarket.service.pokemon.domain.value.CardRarity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class RarityListValidator implements ConstraintValidator<ValidRarityList, String> {

    private String message;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;

        List<String> rarityList = CardRarity.getList();

        String[] values = value.split(",");
        return Arrays.stream(values)
                .allMatch(v -> rarityList.stream()
                        .anyMatch(r -> r.equalsIgnoreCase(v.trim())));
    }
}
