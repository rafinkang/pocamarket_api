package com.venvas.pocamarket.common.aop.trim;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class TrimAspect {

    /**
     * String 타입 필드들의 앞뒤 공백을 제거
     * @param joinPoint AOP 대상 메서드의 실행 정보
     * @return 메서드 실행 결과
     * @throws Throwable trim 처리 중 발생할 수 있는 예외
     */
    @Around("@within(TrimInput) || @annotation(TrimInput)")
    public Object trimStringFields(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // @SkipTrimInput 있으면 종료
        if (method.isAnnotationPresent(SkipTrimInput.class)) {
            return joinPoint.proceed();
        }

        // 메서드 파라미터 배열 가져오기
        Object[] args = joinPoint.getArgs();

        // 각 파라미터에 대해 처리
        for (Object arg : args) {
            // null인 파라미터는 건너뛰기
            if(arg == null) continue;

            // 해당 객체의 모든 필드를 순회하면서 String 타입만 처리
            Arrays.stream(arg.getClass().getDeclaredFields())
                    .filter(field -> field.getType().equals(String.class))
                    .forEach(field -> {
                        // private 필드에 접근하기 위해 접근제어 해제
                        field.setAccessible(true);
                        try {
                            // 필드값 가져오기
                            String value = (String) field.get(arg);
                            // null이 아닌 문자열만 trim 처리
                            if(value != null) {
                                field.set(arg, value.trim());
                            }
                        } catch (IllegalAccessException e) {
                            log.error("필드 trim 처리 중 오류 발생: {}", field.getName(), e);
                            throw new RuntimeException("trim 처리 실패", e);
                        }
                    });
        }
        // 원본 메서드 실행 및 결과 반환
        return joinPoint.proceed(args);
    }
}
