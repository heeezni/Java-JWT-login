package com.ssg.jwtlogin.controller;

import com.ssg.jwtlogin.domain.CustomUserDetails;
import com.ssg.jwtlogin.model.member.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MemberController {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestParam("id") String username, @RequestParam("pwd") String password) {
        log.debug("로그인 요청받음");

        // 유저 꺼내기
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        // 추출한 유저의 비밀번호 - 파라미터로 넘어온 유저의 비번 비교
        if (customUserDetails != null && passwordEncoder.matches(password, customUserDetails.getPassword())) {
            // 로그인 성공하였으므로, JWT 발급

        }

        return null;
    }
}
