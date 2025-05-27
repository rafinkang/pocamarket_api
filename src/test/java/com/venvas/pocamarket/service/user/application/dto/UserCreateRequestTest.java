package com.venvas.pocamarket.service.user.application.dto;

// Jakarta Validation API - Bean Validation을 위한 인터페이스와 클래스
import jakarta.validation.ConstraintViolation;  // 검증 위반 정보를 담는 클래스
import jakarta.validation.Validation;          // Validator 팩토리를 생성하는 유틸리티 클래스
import jakarta.validation.Validator;           // 객체 검증을 수행하는 인터페이스
import jakarta.validation.ValidatorFactory;    // Validator 인스턴스를 생성하는 팩토리

// JUnit 5 어노테이션 - 테스트 프레임워크 관련 어노테이션
import org.junit.jupiter.api.BeforeEach;       // 각 테스트 메소드 실행 전에 실행할 메소드 지정
import org.junit.jupiter.api.DisplayName;      // 테스트에 표시 이름을 부여
import org.junit.jupiter.api.Nested;           // 중첩 테스트 클래스 정의
import org.junit.jupiter.api.Test;             // 테스트 메소드 지정
import org.junit.jupiter.params.ParameterizedTest;  // 파라미터화된 테스트 메소드 지정
import org.junit.jupiter.params.provider.ValueSource;  // 테스트 파라미터 값 제공

import java.util.Set;

// AssertJ 정적 임포트 - 테스트 어서션(검증)을 위한 메소드
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserCreateRequest DTO의 검증 규칙(Bean Validation)을 테스트하는 클래스
 * 이 테스트는 Jakarta Bean Validation을 사용하여 DTO의 유효성 검증 규칙이 올바르게 작동하는지 확인합니다.
 */
class UserCreateRequestTest {

    // Bean Validation API의 검증기 - 객체의 제약 조건 위반을 검사하는 데 사용
    private Validator validator;
    
    // 테스트에 사용할 유효한 요청 객체 - 기본적으로 모든 필드가 유효한 값으로 설정됨
    private UserCreateRequest validRequest;

    /**
     * 각 테스트 메소드 실행 전에 호출되는 설정 메소드
     * Validator를 초기화하고 유효한 요청 객체를 생성합니다.
     */
    @BeforeEach
    void setUp() {
        // Jakarta Bean Validation의 기본 ValidatorFactory 생성
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // 유효한 속성을 가진 테스트용 요청 객체 생성
        validRequest = new UserCreateRequest();
        setValidFields(validRequest);
    }
    
    /**
     * UserCreateRequest 객체의 모든 필드에 유효한 값을 설정하는 헬퍼 메소드
     * DTO 클래스가 setter 메소드를 제공하지 않기 때문에 리플렉션을 사용하여 private 필드에 직접 접근합니다.
     * 
     * @param request 값을 설정할 UserCreateRequest 객체
     */
    private void setValidFields(UserCreateRequest request) {
        // 리플렉션을 사용하여 private 필드에 값 설정
        // 리플렉션: 런타임에 클래스의 구조, 필드, 메소드 등에 접근하는 자바 API
        try {
            // 로그인 ID 설정 - 유효한 값: 4~15자 사이
            java.lang.reflect.Field loginIdField = UserCreateRequest.class.getDeclaredField("loginId");
            loginIdField.setAccessible(true);  // private 필드 접근 허용
            loginIdField.set(request, "testuser");
            
            // 비밀번호 설정 - 유효한 값: 8~20자, 숫자/영문자/특수문자 포함
            java.lang.reflect.Field passwordField = UserCreateRequest.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(request, "Test1234!");
            
            // 이름 설정 - 유효한 값: 빈 값이 아니고 10자 이하
            java.lang.reflect.Field nameField = UserCreateRequest.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(request, "테스트 사용자");
            
            // 닉네임 설정 - 유효한 값: 빈 값이 아니고 8자 이하
            java.lang.reflect.Field nicknameField = UserCreateRequest.class.getDeclaredField("nickname");
            nicknameField.setAccessible(true);
            nicknameField.set(request, "닉네임");
            
            // 이메일 설정 - 유효한 값: 이메일 형식, 30자 이하 (필수 아님)
            java.lang.reflect.Field emailField = UserCreateRequest.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(request, "test@example.com");
            
            // 전화번호 설정 - 유효한 값: 전화번호 형식 (xxx-xxxx-xxxx), 20자 이하 (필수 아님)
            java.lang.reflect.Field phoneField = UserCreateRequest.class.getDeclaredField("phone");
            phoneField.setAccessible(true);
            phoneField.set(request, "010-1234-5678");
            
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }
    }

    /**
     * 로그인 ID 필드의 유효성 검증 테스트 그룹
     * @Nested: 중첩 테스트 클래스로 관련 테스트를 그룹화하고 계층적으로 구성
     * @DisplayName: 테스트 보고서와 실행 결과에 표시될 이름 지정
     */
    @Nested
    @DisplayName("로그인 ID 유효성 검증")
    class LoginIdValidationTest {

        /**
         * 유효한 로그인 ID를 가진 요청 객체가 검증을 통과하는지 테스트
         * @Test: 이 메소드가 JUnit 테스트 메소드임을 표시
         * @DisplayName: 테스트의 목적을 설명하는 이름 제공
         */
        @Test
        @DisplayName("유효한 로그인 ID")
        void validLoginId() {
            // validator.validate()는 객체의 모든 제약 조건을 검사하고 위반 사항을 반환
            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            // 유효한 객체이므로 위반 사항이 없어야 함
            assertThat(violations).isEmpty();
        }

        /**
         * 빈 로그인 ID를 가진 요청 객체가 검증 실패하는지 테스트
         * @ParameterizedTest: 여러 입력 값으로 동일한 테스트를 반복 실행
         * @ValueSource: 테스트에 사용할 입력 값 배열 제공 (여기서는 빈 문자열과 공백 문자열)
         * @DisplayName: 테스트의 목적을 설명하는 이름 제공
         * 
         * @param loginId 테스트할 로그인 ID 값 (파라미터로 자동 주입됨)
         */
        @ParameterizedTest
        @ValueSource(strings = {"", " "})  // 빈 문자열과 공백 문자열을 테스트 파라미터로 사용
        @DisplayName("빈 로그인 ID")
        void emptyLoginId(String loginId) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("loginId");
                field.setAccessible(true);
                field.set(validRequest, loginId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loginId"))).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "ab", "abc"})
        @DisplayName("최소 길이 미만 로그인 ID")
        void tooShortLoginId(String loginId) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("loginId");
                field.setAccessible(true);
                field.set(validRequest, loginId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loginId"))).isTrue();
        }

        @Test
        @DisplayName("최대 길이 초과 로그인 ID")
        void tooLongLoginId() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("loginId");
                field.setAccessible(true);
                field.set(validRequest, "abcdefghijklmnopq"); // 16자
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loginId"))).isTrue();
        }
    }

    /**
     * 비밀번호 필드의 유효성 검증 테스트 그룹
     * 비밀번호는 8~20자 사이이며, 숫자, 영문자, 특수문자를 각각 1개 이상 포함해야 함
     */
    @Nested
    @DisplayName("비밀번호 유효성 검증")
    class PasswordValidationTest {

        @Test
        @DisplayName("유효한 비밀번호")
        void validPassword() {
            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("빈 비밀번호")
        void emptyPassword(String password) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("password");
                field.setAccessible(true);
                field.set(validRequest, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Test123", "1234567"})
        @DisplayName("최소 길이 미만 비밀번호")
        void tooShortPassword(String password) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("password");
                field.setAccessible(true);
                field.set(validRequest, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
        }

        @Test
        @DisplayName("숫자가 없는 비밀번호")
        void passwordWithoutDigits() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("password");
                field.setAccessible(true);
                field.set(validRequest, "Testtest!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
        }

        @Test
        @DisplayName("영문자가 없는 비밀번호")
        void passwordWithoutLetters() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("password");
                field.setAccessible(true);
                field.set(validRequest, "12345678!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
        }

        @Test
        @DisplayName("특수문자가 없는 비밀번호")
        void passwordWithoutSpecialChars() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("password");
                field.setAccessible(true);
                field.set(validRequest, "Test1234");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
        }
    }

    /**
     * 이메일 필드의 유효성 검증 테스트 그룹
     * 이메일은 필수가 아니지만, 값이 있을 경우 올바른 이메일 형식이어야 함
     */
    @Nested
    @DisplayName("이메일 유효성 검증")
    class EmailValidationTest {

        @Test
        @DisplayName("유효한 이메일")
        void validEmail() {
            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("이메일 없음 - 허용됨")
        void nullEmail() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("email");
                field.setAccessible(true);
                field.set(validRequest, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"test", "test@", "test@example", "test@.com", "@example.com"})
        @DisplayName("잘못된 이메일 형식")
        void invalidEmailFormat(String email) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("email");
                field.setAccessible(true);
                field.set(validRequest, email);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
        }
    }

    /**
     * 전화번호 필드의 유효성 검증 테스트 그룹
     * 전화번호는 필수가 아니지만, 값이 있을 경우 올바른 전화번호 형식(xxx-xxxx-xxxx)이어야 함
     */
    @Nested
    @DisplayName("전화번호 유효성 검증")
    class PhoneValidationTest {

        @Test
        @DisplayName("유효한 전화번호")
        void validPhone() {
            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("전화번호 없음 - 허용됨")
        void nullPhone() {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("phone");
                field.setAccessible(true);
                field.set(validRequest, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"01012345678", "010-123-5678", "010-12345-678", "010-1234-56789"})
        @DisplayName("잘못된 전화번호 형식")
        void invalidPhoneFormat(String phone) {
            try {
                java.lang.reflect.Field field = UserCreateRequest.class.getDeclaredField("phone");
                field.setAccessible(true);
                field.set(validRequest, phone);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(validRequest);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone"))).isTrue();
        }
    }
}
