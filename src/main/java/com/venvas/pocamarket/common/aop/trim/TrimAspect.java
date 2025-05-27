package com.venvas.pocamarket.common.aop.trim;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class TrimAspect {

    @Before("@annotation(TrimInput)")
    public void trimStringFields(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if(arg == null) continue;

            Arrays.stream(arg.getClass().getDeclaredFields())
                    .filter(field -> field.getType().equals(String.class))
                    .forEach(field -> {
                        field.setAccessible(true);      // 접근 가능 설정
                        try {
                            String value = (String) field.get(arg);
                            if(value != null) {
                                field.set(arg, value.trim());
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
