package com.venvas.pocamarket.service.user.domain.entity;

// 애플리케이션 계층 클래스
import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;  // 사용자 생성 요청 DTO

// 도메인 계층 클래스
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;  // 사용자 등급 열거형
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;  // 사용자 상태 열거형

// JUnit 5 테스트 관련 어노테이션
import org.junit.jupiter.api.DisplayName;  // 테스트에 표시 이름을 부여하는 어노테이션
import org.junit.jupiter.api.Nested;       // 테스트 클래스 내에 중첩 테스트 클래스를 정의하는 어노테이션
import org.junit.jupiter.api.Test;         // 테스트 메소드를 정의하는 어노테이션

// 어서션(검증) 및 모의 객체 관련 정적 임포트
import static org.assertj.core.api.Assertions.assertThat;  // 값 검증을 위한 메소드
import static org.mockito.Mockito.mock;  // 모의 객체 생성을 위한 메소드
import static org.mockito.Mockito.when;   // 모의 객체의 동작을 정의하는 메소드

/**
 * User 엔티티 클래스의 단위 테스트
 * 사용자 엔티티의 생성, 열거형 처리, 이메일 검증 등의 기능을 테스트합니다.
 * 도메인 모델의 동작이 올바르게 구현되었는지 확인하는 테스트로, 외부 의존성없이 객체 자체만 테스트합니다.
 */
class UserTest {

    /**
     * 사용자 엔티티 생성에 관한 테스트 그룹
     * 팩토리 메소드를 통한 사용자 엔티티 생성이 올바르게 동작하는지 검증합니다.
     * @Nested: 테스트 클래스를 논리적으로 그룹화하여 관련 테스트를 중첩으로 구성
     * @DisplayName: 테스트 결과 보고서에 표시될 이름 지정
     */
    @Nested
    @DisplayName("사용자 엔티티 생성 테스트")
    class CreateUserTest {

        /**
         * 팩토리 메소드를 통한 사용자 생성이 성공하는지 테스트
         * UserCreateRequest와 암호화된 비밀번호를 사용하여 User 엔티티가 올바르게 생성되는지 확인합니다.
         * @Test: JUnit 테스트 메소드를 정의
         * @DisplayName: 테스트 결과 보고서에 표시될 이름 지정
         */
        @Test
        @DisplayName("팩토리 메서드를 통한 사용자 생성 성공")
        void createFromRequestSuccess() {
            // given: 테스트를 위한 사전 조건 설정
            // 모의 사용자 생성 요청 객체 생성
            UserCreateRequest request = mock(UserCreateRequest.class);
            // 가짜 요청 객체의 동작 정의 - 호출 시 반환할 값들 설정
            when(request.getLoginId()).thenReturn("testuser");       // 로그인 ID
            when(request.getName()).thenReturn("테스트 사용자");   // 이름
            when(request.getNickname()).thenReturn("닉네임");     // 닉네임
            when(request.getEmail()).thenReturn("test@example.com"); // 이메일
            when(request.getPhone()).thenReturn("010-1234-5678");   // 전화번호

            // 암호화된 비밀번호(가정)
            String encodedPassword = "encoded_password";

            // when: 테스트할 기능 실행 - 팩토리 메소드를 통한 사용자 엔티티 생성
            User user = User.createFromRequest(request, encodedPassword);

            // then: 결과 검증 - 생성된 엔티티의 속성들이 예상한 값인지 확인
            assertThat(user).isNotNull();  // 객체가 생성되었는지 확인
            assertThat(user.getUuid()).isNotNull();  // UUID가 자동 생성되었는지 확인
            assertThat(user.getLoginId()).isEqualTo("testuser");  // 로그인 ID 값 확인
            assertThat(user.getPassword()).isEqualTo("encoded_password");  // 암호화된 비밀번호 확인
            assertThat(user.getName()).isEqualTo("테스트 사용자");  // 이름 확인
            assertThat(user.getNickname()).isEqualTo("닉네임");  // 닉네임 확인
            assertThat(user.getEmail()).isEqualTo("test@example.com");  // 이메일 확인
            assertThat(user.getPhone()).isEqualTo("010-1234-5678");  // 전화번호 확인
            assertThat(user.getEmailVerified()).isFalse();  // 초기 이메일 인증 상태는 false여야 함
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);  // 초기 상태는 ACTIVE여야 함
            assertThat(user.getGrade()).isEqualTo(UserGrade.REGULAR);  // 초기 등급은 REGULAR여야 함
        }
    }

    /**
     * 열거형 처리에 관한 테스트 그룹
     * 사용자 상태와 등급 열거형을 간에 정수 코드와 열거형 값의 변환이 올바르게 동작하는지 검증합니다.
     */
    @Nested
    @DisplayName("열거형 처리 테스트")
    class EnumHandlingTest {

        /**
         * 사용자 상태(UserStatus) 열거형과 DB 상태 코드 간의 변환이 정상적으로 동작하는지 테스트
         * statusCode(Integer) 값으로부터 열거형을 가져오고, 열거형으로부터 코드를 가져오는 과정을 테스트
         */
        @Test
        @DisplayName("상태 코드 변환 테스트")
        void statusConversionTest() {
            // given: 테스트를 위한 사전 조건 설정
            // INACTIVE 상태코드를 가진 사용자 엔티티 생성
            User user = User.builder()
                    .statusCode(UserStatus.INACTIVE.getCode())  // 비활성 상태 코드 설정
                    .build();

            // when & then: 상태 코드로부터 열거형을 정확히 가져오는지 검증
            // getStatus()는 statusCode를 사용하여 해당하는 UserStatus 열거형을 반환해야 함
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);  // 정수 코드로부터 열거형 변환 확인

            // when: 열거형을 통해 상태 변경
            user.setStatus(UserStatus.ACTIVE);  // 상태를 ACTIVE로 변경

            // then: 열거형이 정수 코드로 정확히 변환되었는지 검증
            assertThat(user.getStatusCode()).isEqualTo(UserStatus.ACTIVE.getCode());  // 열거형이 코드로 변환되었는지 확인
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);  // 열거형 값도 정확한지 확인
        }

        /**
         * 사용자 등급(UserGrade) 열거형과 DB 등급 코드 간의 변환이 정상적으로 동작하는지 테스트
         * gradeCode(Integer) 값으로부터 열거형을 가져오고, 열거형으로부터 코드를 가져오는 과정을 테스트
         */
        @Test
        @DisplayName("등급 코드 변환 테스트")
        void gradeConversionTest() {
            // given: 테스트를 위한 사전 조건 설정
            // VIP 등급 코드를 가진 사용자 엔티티 생성
            User user = User.builder()
                    .gradeCode(UserGrade.VIP.getCode())  // VIP 등급 코드 설정
                    .build();

            // when & then: 등급 코드로부터 열거형을 정확히 가져오는지 검증
            // getGrade()는 gradeCode를 사용하여 해당하는 UserGrade 열거형을 반환해야 함
            assertThat(user.getGrade()).isEqualTo(UserGrade.VIP);  // 정수 코드로부터 열거형 변환 확인

            // when: 열거형을 통해 등급 변경
            user.setGrade(UserGrade.REGULAR);  // 등급을 REGULAR로 변경

            // then: 열거형이 정수 코드로 정확히 변환되었는지 검증
            assertThat(user.getGradeCode()).isEqualTo(UserGrade.REGULAR.getCode());  // 열거형이 코드로 변환되었는지 확인
            assertThat(user.getGrade()).isEqualTo(UserGrade.REGULAR);  // 열거형 값도 정확한지 확인
        }
    }

    /**
     * 이메일 검증 관련 테스트 그룹
     * 이메일 인증 상태 변경이 정상적으로 동작하는지 검증합니다.
     */
    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailVerificationTest {

        /**
         * 이메일 인증 상태 변경이 정상적으로 동작하는지 테스트
         * 초기에 검증되지 않은 상태(false)에서 검증된 상태(true)로 변경되는 과정을 테스트
         */
        @Test
        @DisplayName("이메일 인증 상태 변경 테스트")
        void emailVerificationStatusChangeTest() {
            // given: 테스트를 위한 사전 조건 설정
            // 초기 이메일 검증이 false로 설정된 사용자 엔티티 생성
            User user = User.builder()
                    .emailVerified(false)  // 초기에는 이메일이 검증되지 않은 상태
                    .build();

            // when: 테스트할 기능 실행 - 이메일 검증 상태를 true로 변경
            user.setEmailVerified(true);  // 이메일 검증 완료 상태로 변경

            // then: 결과 검증 - 이메일 검증 상태가 정확히 변경되었는지 확인
            assertThat(user.getEmailVerified()).isTrue();  // 이메일 검증 상태가 true로 변경되었는지 확인
        }
    }
}
