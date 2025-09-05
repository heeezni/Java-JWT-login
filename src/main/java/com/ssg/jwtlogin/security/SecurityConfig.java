package com.ssg.jwtlogin.security;

import com.ssg.jwtlogin.model.member.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티를 개발자가 원하는 설정으로 변경하고자 할 때 이 클래스 정의 필요
 * */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 시큐리티가 사용할 서비스 객체 등록
    private final CustomUserDetailsService customUserDetailsService;

    // DaoAuthenticationProvider가 사용할 비번 검증 인코더 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 결과물 자체에 salt가 포함됨
    }

    // 아이디와 패스워드를 자동으로 비교해주는 AuthenticationProvider등록
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        // AuthenticationProvider가 사용할 서비스 객체 등록
        daoAuthProvider.setUserDetailsService(customUserDetailsService);
        // AuthenticationProvider가 사용할 비밀번호 인코더
        daoAuthProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthProvider;
    }

    // AuthenticationProvider들 중 알맞은 것을 선택해 위임하는 AuthenticationManager 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                /** Spring Security는 기본적으로 CSRF 방지 기능이 있음
                 * 아래의 코드로 disable시키면 CSRF 비활성화
                 * 전통적인 form 로그인/세션 방식이 아니라 REST/JWT로 인증을 처리할 때는 보통 끔
                  */
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(form->form
                .loginPage("/member/login.html") // 커스텀 로그인 페이지 GET
                .loginProcessingUrl("/member/login") // 로그인 처리 POST(스프링이 핸들)
            )
                .build();
    }

}
