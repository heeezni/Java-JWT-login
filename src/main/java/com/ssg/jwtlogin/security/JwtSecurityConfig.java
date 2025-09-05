package com.ssg.jwtlogin.security;

import com.ssg.jwtlogin.filter.JwtAuthFilter;
import com.ssg.jwtlogin.model.member.CustomUserDetailsService;
import com.ssg.jwtlogin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public OncePerRequestFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 서비스를 이용하여 유저 정보를 가져와야 하므로, 서비스 등록
        provider.setUserDetailsService(customUserDetailsService);
        // 사용하게 될 비밀번호 인코더
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Spring Security의 기본 인증 흐름(세션/폼 로그인/CSRF 방어)을 모두 꺼버리고,
     * 오직 JWT 토큰 기반 인증만 사용하도록 완전히 무상태(stateless) 구조로 바꾸기
     * */
    @Bean
    public SecurityFilterChain customSecurityFilterChain(
                                                            HttpSecurity http,
                                                            OncePerRequestFilter jwtAuthFilter,
                                                            DaoAuthenticationProvider provider) throws Exception {
        http
                // REST/JWT 사용 시 비활성화
                .csrf(csrf -> csrf.disable())
                // 세션을 만들지도, 사용하지도 않도록 정책을 STATELESS로 설정
                // 시큐리티가 인증상태를 더 이상 세선에 저장하지 않게 함
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // SecurityContext 저장소를 null 저장소로 교체 ( 인증 결과를 세션/서버 상태에 저장하지 않음 - 완전 무상태 저장)
                .securityContext(sc->sc
                        .securityContextRepository(new NullSecurityContextRepository()))
                // 시큐리티의 폼로그인 기능 : 컨트롤러없이도 로그인 인증 처리됨
                // JWT에서는 개발자가 토큰을 컨트롤러에서 발급해야하므로, 컨트롤러 활성화 시키고, 대신 form 로그인 기본 기능 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/member/login.html").permitAll() // 로그인 폼에 대해 해제
                        .requestMatchers("/member/login").permitAll() // 로그인 요청에 대해 해제
                        .anyRequest().authenticated()
                        )
                .authenticationProvider(provider)
                .addFilterBefore(jwtAuthFilter, AuthorizationFilter.class);
        return http.build();
    }
}