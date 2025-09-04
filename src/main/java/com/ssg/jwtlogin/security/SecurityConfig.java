package com.ssg.jwtlogin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티를 개발자가 원하는 설정으로 변경하고자 할 때 이 클래스 정의 필요
 * */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 현재 DB에 들어간 비밀번호는 BCrypt 알고리즘이므로, Security에게 같은 알고리즘을 사용하도록 알려주기
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(AbstractHttpConfigurer::disable).build();
    }

}
