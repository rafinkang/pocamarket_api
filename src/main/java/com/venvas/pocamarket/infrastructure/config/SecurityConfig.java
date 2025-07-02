package com.venvas.pocamarket.infrastructure.config;

import com.venvas.pocamarket.infrastructure.security.JwtAccessDeniedHandler;
import com.venvas.pocamarket.infrastructure.security.JwtAuthenticationEntryPoint;
import com.venvas.pocamarket.infrastructure.security.JwtAuthenticationFilter;
import com.venvas.pocamarket.infrastructure.util.JwtTokenProvider;
import com.venvas.pocamarket.service.user.domain.repository.RefreshTokenRepository;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정 클래스
 * 애플리케이션의 보안 관련 설정을 담당
 */
@Configuration // 스프링 설정 클래스임을 명시
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 웹 보안 활성화
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인증, 권한 부족시 실행 되는 핸들러 등록
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));

        // URL별 접근 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        // 구체적인 경로를 먼저, 아닌 경로를 나중에

                        // 인증 필요한 경로
                        .requestMatchers(
                                "/tcg-trade/my/**",
                                "/tcg-trade/refresh/**")
                        .authenticated()

                        // 인증 없이 접근 가능한 공개 API
                        .requestMatchers(
                                "/login",
                                "/reissue",
                                "/register",
                                "/tcg-trade/**",
                                "/pokemon-card/**")
                        .permitAll()

                        // ADMIN 권한 체크
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/pokemon-card/update/card/*/*",
                                "/swagger-ui.html")
                        .hasRole("ADMIN")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated());

        // jwtAuthFilter를 filterChain에 추가 - 인증이 필요한 경로에만 적용
        http
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenRepository,
                                userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 해당 경로 security filter chain을 생략
     * 
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring()
                    // /static/**, /css/**, /js/**, /images/** 등은 허용
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations());

        };
    }

    /**
     * Spring Security 필터 체인 내에서의 CORS 설정을 담당
     * 인증이 필요한 요청에 대한 CORS 설정을 처리
     * 특히 인증된 요청이나 프리플라이트(preflight) 요청에 대한 처리가 필요할 때 중요
     * Security 컨텍스트 내에서 CORS를 처리하기 때문에 보안 관련 헤더나 인증 정보가 포함된 요청을 처리하기에 적합
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost"); // 허용할 프론트엔드 출처
        configuration.addAllowedOrigin("https://localhost"); // 허용할 프론트엔드 출처
        configuration.addAllowedOrigin("https://pocamarket.co.kr"); // 허용할 프론트엔드 출처
        configuration.addAllowedOrigin("https://www.pocamarket.co.kr"); // 허용할 프론트엔드 출처
        configuration.addAllowedMethod("GET"); // 허용할 HTTP 메서드들을 개별적으로 설정
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용
        configuration.setMaxAge(3600L); // preflight 요청 캐시 시간 (초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // CORS를 적용할 경로 패턴 지정
        return source;
    }
}