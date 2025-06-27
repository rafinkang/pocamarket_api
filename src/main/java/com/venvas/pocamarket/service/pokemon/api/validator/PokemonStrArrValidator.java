package com.venvas.pocamarket.service.pokemon.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class PokemonStrArrValidator implements ConstraintValidator<PokemonStrArrParam, String> {

    private String expectedPokemonListPath;

    @Override
    public void initialize(PokemonStrArrParam constraintAnnotation) {
        this.expectedPokemonListPath = constraintAnnotation.expectedPokemonListPath();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;

        try {
            Class<?> clazz = Class.forName(expectedPokemonListPath);
            Method method = clazz.getMethod("getList");

            Object result = method.invoke(null);
            if (result instanceof List) {
                List<String> expectedList = (List<String>) result;

                String[] values = value.split(",");
                return Arrays.stream(values)
                        .allMatch(v -> expectedList.stream()
                                .anyMatch(r -> r.equalsIgnoreCase(v.trim())));
            } else {
                throw new IllegalStateException("메서드 반환 타입이 List<String>가 아닙니다.");
            }

        } catch (Exception e) {
            return false;
        }
    }
}
