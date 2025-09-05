package com.ssg.jwtlogin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.NullSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
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
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}