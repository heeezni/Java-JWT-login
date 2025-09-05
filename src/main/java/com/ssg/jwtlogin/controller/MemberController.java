package com.ssg.jwtlogin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MemberController {

    private final AuthenticationManager authManager;

    @PostMapping("/member/login")
    public ResponseEntity<?> login() {
        log.debug("로그인 요청 처리를 개발자가 정의한 컨트롤러에서 처리함");
        // 사용자 인증 성공이 되면, 토큰 발급
        // 사용자의 아이디와 패스워드를 검증하는 시큐리티 객체는 DaoAuthenticationProvider,
        // 이 객체가 검증을 시도하게 하려면, AuthenticationManager가 보유한 Authenticate()메서드를 호출하면 됨
        return null;
    }
}
