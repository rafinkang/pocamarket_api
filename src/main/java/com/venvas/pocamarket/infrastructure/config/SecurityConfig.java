package com.venvas.pocamarket.infrastructure.config;

import com.venvas.pocamarket.infrastructure.security.JwtAccessDeniedHandler;
import com.venvas.pocamarket.infrastructure.security.JwtAuthenticationEntryPoint;
import com.venvas.pocamarket.infrastructure.security.JwtAuthenticationFilter;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * 애플리케이션의 보안 관련 설정을 담당
 */
@Configuration  // 스프링 설정 클래스임을 명시
@RequiredArgsConstructor
@EnableWebSecurity  // Spring Security 웹 보안 활성화
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
     * BCryptPasswordEncoder는 비밀번호를 해시화하는데 사용되는 구현체
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 HTTP 보안 설정
     * URL별 접근 권한, CSRF 설정, 인증 방식 등을 정의
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF(Cross-Site Request Forgery) 보호 설정
            // 개발 환경에서는 비활성화, 운영 환경에서는 활성화 권장
            .csrf(AbstractHttpConfigurer::disable)
            // HTTP Basic 인증 비활성화
            .httpBasic(AbstractHttpConfigurer::disable);

        // 세션 관리 상태 없음으로 구성
        http
            .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인증, 권한 부족시 실행 되는 핸들러 등록
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // URL별 접근 권한 설정
        http
            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/pokemon-cards/update/card/**").hasRole("ADMIN")// 관리자 접근 가능 API

                // /api/** 경로는 인증 없이 접근 가능 (permitAll)
                // 예: /api/user/create, /api/pokemon/list 등
                .requestMatchers(
                        "/api/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html"
                ).permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );

        // jwtAuthFilter를 filterChain에 추가
        http
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * /static/**, /css/**, /js/**, /images/** 등은 허용
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
} 