package com.venvas.pocamarket.service.user.application.service;

// 애플리케이션 계층 클래스
import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;

// 도메인 계층 클래스
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;

// JUnit 5 테스트 관련 어노테이션
import org.junit.jupiter.api.BeforeEach;      // 각 테스트 전 초기화 작업을 위한 어노테이션
import org.junit.jupiter.api.DisplayName;     // 테스트에 이름을 부여하는 어노테이션
import org.junit.jupiter.api.Nested;          // 테스트 클래스 내에 중첩 테스트 클래스를 정의하는 어노테이션
import org.junit.jupiter.api.Test;            // 테스트 메서드를 정의하는 어노테이션
import org.junit.jupiter.api.extension.ExtendWith;  // JUnit 확장 기능을 사용하기 위한 어노테이션

// Mockito 관련 어노테이션
import org.mockito.InjectMocks;   // 테스트 대상 클래스에 Mock 객체들을 주입하는 어노테이션
import org.mockito.Mock;          // 가짜(Mock) 객체를 생성하는 어노테이션
import org.mockito.junit.jupiter.MockitoExtension;  // Mockito를 JUnit 5와 통합하는 확장 기능

// Spring 관련 클래스
import org.springframework.security.crypto.password.PasswordEncoder;  // 비밀번호 암호화 인터페이스

// 정적 임포트 - 테스트 코드 가독성을 높여주는 메서드들
import static org.assertj.core.api.Assertions.assertThat;           // 값 검증을 위한 메서드
import static org.assertj.core.api.Assertions.assertThatThrownBy;   // 예외 발생 검증을 위한 메서드
import static org.mockito.ArgumentMatchers.any;                     // 모든 타입의 인자를 매칭하는 메서드
import static org.mockito.ArgumentMatchers.anyString;               // 모든 문자열 인자를 매칭하는 메서드
import static org.mockito.Mockito.*;                               // Mockito의 정적 메서드들

/**
 * UserService 클래스의 단위 테스트
 * 사용자 생성, 유효성 검증 등의 기능을 테스트합니다.
 * 
 * @ExtendWith(MockitoExtension.class): JUnit 5와 Mockito를 통합하는 확장 기능을 사용합니다.
 * 이를 통해 @Mock, @InjectMocks 등의 어노테이션을 사용할 수 있습니다.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * 테스트에 사용될 사용자 저장소 모의 객체
     * @Mock: Mockito가 가짜 객체를 생성하여 동작을 정의할 수 있게 합니다.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * 테스트에 사용될 비밀번호 암호화 모의 객체
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * 테스트 대상 서비스 클래스
     * @InjectMocks: @Mock으로 선언된 모의 객체들을 이 클래스에 자동으로 주입합니다.
     */
    @InjectMocks
    private UserService userService;

    /**
     * 사용자 생성 기능에 관한 테스트 그룹
     * @Nested: 테스트 클래스 내부에 중첩된 테스트 클래스를 정의하여 관련 테스트를 논리적으로 그룹화
     * @DisplayName: 테스트의 이름을 지정하여 테스트 결과 보고서에 표시
     */
    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTest {

        /**
         * 각 테스트 실행 전 초기화 작업을 수행하는 메소드
         * @BeforeEach: 각 테스트 메소드 실행 전에 자동으로 호출되어 테스트 환경을 초기화
         */
        @BeforeEach
        void setUp() {
            // 비밀번호 암호화 모의 설정 - 어떤 문자열이 입력되든 'encoded_password'를 반환
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
            
            // 저장된 사용자 모의 설정 - save() 메소드 호출 시 반환할 가짜 User 객체
            User savedUser = mock(User.class);
            when(savedUser.getId()).thenReturn(1L);  // 가짜 사용자 ID
            when(savedUser.getLoginId()).thenReturn("testuser");  // 가짜 사용자 로그인 ID
            when(userRepository.save(any(User.class))).thenReturn(savedUser);  // 어떤 User 객체가 저장되든 savedUser 반환
            
            // 중복 체크 모의 설정 - 기본적으로 중복된 값이 없다고 가정
            when(userRepository.existsByLoginId(anyString())).thenReturn(false);  // 로그인 ID 중복 없음
            when(userRepository.existsByEmail(anyString())).thenReturn(false);    // 이메일 중복 없음
        }

        /**
         * 유효한 요청을 사용하여 사용자 생성이 성공하는 경우를 테스트
         * @Test: JUnit 테스트 메소드를 정의
         * @DisplayName: 테스트 실행 결과에 표시될 이름
         */
        @Test
        @DisplayName("유효한 요청으로 사용자 생성 성공")
        void createUserSuccess() {
            // given: 유효한 사용자 요청 객체 생성
            UserCreateRequest request = mock(UserCreateRequest.class);
            when(request.getLoginId()).thenReturn("testuser");
            when(request.getPassword()).thenReturn("Password123!");
            when(request.getName()).thenReturn("테스트 사용자");
            when(request.getNickname()).thenReturn("닉네임");
            when(request.getEmail()).thenReturn("test@example.com");
            when(request.getPhone()).thenReturn("010-1234-5678");
            
            // when: 테스트할 메소드 실행 (테스트 대상의 행동)
            User createdUser = userService.register(request);

            // then: 결과 검증 (예상된 결과와 실제 결과를 비교)
            assertThat(createdUser).isNotNull();  // 객체가 null이 아니어야 함
            assertThat(createdUser.getId()).isEqualTo(1L);  // ID가 예상한 값인지 확인
            assertThat(createdUser.getLoginId()).isEqualTo("testuser");  // 로그인 ID가 예상한 값인지 확인
            
            // 호출 확인: 해당 메소드가 정확한 인자로 호출되었는지 확인
            verify(passwordEncoder).encode("Password123!");  // 비밀번호 암호화 메소드 호출 확인
            verify(userRepository).save(any(User.class));  // 저장소의 save 메소드 호출 확인
            verify(userRepository).existsByLoginId("testuser");  // 로그인 ID 중복 검사 호출 확인
            verify(userRepository).existsByEmail("test@example.com");  // 이메일 중복 검사 호출 확인
        }

        /**
         * 중복된 로그인 ID로 사용자 생성이 실패하는 경우를 테스트
         * 중복된 로그인 ID를 사용하면 DUPLICATE_LOGIN_ID 오류가 발생해야 함
         */
        // @Test
        // @DisplayName("중복된 로그인 ID로 사용자 생성 실패")
        // void createUserFailWithDuplicateLoginId() {
        //     // given: 테스트 사전 조건 설정
        //     UserCreateRequest request = mock(UserCreateRequest.class);
        //     when(request.getLoginId()).thenReturn("testuser");
            
        //     // 로그인 ID가 이미 존재하는 경우로 설정 (중복 확인 함수가 true 반환)
        //     when(userRepository.existsByLoginId("testuser")).thenReturn(true);

        //     // when & then: 예외 발생 검증
        //     assertThatThrownBy(() -> userService.createUser(request))  // 람다 표현식으로 예외 발생 확인
        //             .isInstanceOf(UserException.class)  // UserException 클래스의 예외가 발생해야 함
        //             .matches(e -> ((UserException) e).getErrorCode() == UserErrorCode.DUPLICATE_LOGIN_ID);  // 오류 코드 확인
            
        //     // 메소드 호출 확인
        //     verify(userRepository).existsByLoginId("testuser");  // 로그인 ID 중복 검사 호출 확인
        //     verify(userRepository, never()).save(any(User.class));  // save 메소드가 호출되지 않아야 함 (never)
        // }

        // /**
        //  * 중복된 이메일로 사용자 생성이 실패하는 경우를 테스트
        //  * 중복된 이메일을 사용하면 DUPLICATE_EMAIL 오류가 발생해야 함
        //  */
        // @Test
        // @DisplayName("중복된 이메일로 사용자 생성 실패")
        // void createUserFailWithDuplicateEmail() {
        //     // given: 테스트 사전 조건 설정
        //     UserCreateRequest request = mock(UserCreateRequest.class);
        //     when(request.getLoginId()).thenReturn("testuser");
        //     when(request.getEmail()).thenReturn("test@example.com");
            
        //     // 이메일이 이미 존재하는 경우로 설정 (중복 확인 함수가 true 반환)
        //     when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        //     // when & then: 예외 발생 검증
        //     assertThatThrownBy(() -> userService.createUser(request))  // 람다 표현식으로 예외 발생 확인
        //             .isInstanceOf(UserException.class)  // UserException 클래스의 예외가 발생해야 함
        //             .matches(e -> ((UserException) e).getErrorCode() == UserErrorCode.DUPLICATE_EMAIL);  // 오류 코드 확인
            
        //     // 메소드 호출 확인
        //     verify(userRepository).existsByLoginId("testuser");  // 로그인 ID 중복 검사 호출 확인
        //     verify(userRepository).existsByEmail("test@example.com");  // 이메일 중복 검사 호출 확인
        //     verify(userRepository, never()).save(any(User.class));  // save 메소드가 호출되지 않아야 함 (never)
        // }

        // @Test
        // @DisplayName("이메일 없이 사용자 생성 성공")
        // void createUserSuccessWithoutEmail() {
        //     // given
        //     UserCreateRequest request = mock(UserCreateRequest.class);
        //     when(request.getLoginId()).thenReturn("testuser");
        //     when(request.getPassword()).thenReturn("Password123!");
        //     when(request.getName()).thenReturn("테스트 사용자");
        //     when(request.getNickname()).thenReturn("닉네임");
        //     when(request.getEmail()).thenReturn(null);
            
        //     // when
        //     User createdUser = userService.createUser(request);

        //     // then
        //     assertThat(createdUser).isNotNull();
        //     verify(userRepository, never()).existsByEmail(anyString());
        // }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("로그인 ID 형식 검증 실패 - 공백 포함")
        void validateLoginIdFailWithSpace() {
            // given
            UserCreateRequest request = mock(UserCreateRequest.class);
            when(request.getLoginId()).thenReturn("test user");
            
            // when & then
            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(UserException.class)
                    .matches(e -> ((UserException) e).getErrorCode() == UserErrorCode.INVALID_LOGIN_ID_FORMAT);
        }

        @Test
        @DisplayName("로그인 ID 형식 검증 실패 - @ 포함")
        void validateLoginIdFailWithAtSign() {
            // given
            UserCreateRequest request = mock(UserCreateRequest.class);
            when(request.getLoginId()).thenReturn("test@user");

            // when & then
            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(UserException.class)
                    .matches(e -> ((UserException) e).getErrorCode() == UserErrorCode.INVALID_LOGIN_ID_FORMAT);
        }

        @Test
        @DisplayName("닉네임 검증 실패 - 금지된 단어 포함")
        void validateNicknameFailWithProhibitedWord() {
            // given
            UserCreateRequest request = mock(UserCreateRequest.class);
            when(request.getLoginId()).thenReturn("testuser");
            when(request.getNickname()).thenReturn("관리자닉네임");

            // when & then
            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(UserException.class)
                    .matches(e -> ((UserException) e).getErrorCode() == UserErrorCode.INVALID_NICKNAME);
        }
    }
}
