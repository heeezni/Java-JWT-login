package com.ssg.jwtlogin.controller;

import com.ssg.jwtlogin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MemberController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestParam("id") String username, @RequestParam("pwd") String password) {
        log.debug("로그인 요청 처리를 개발자가 정의한 컨트롤러에서 처리함");
        // 사용자 인증 성공이 되면, 토큰 발급
        // 사용자의 아이디와 패스워드를 검증하는 시큐리티 객체는 DaoAuthenticationProvider,
        // 이 객체가 검증을 시도하게 하려면, AuthenticationManager가 보유한 authenticate()메서드를 호출하면 됨
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authManager.authenticate(token); // DaoAuthenticationProvider 동작시키기
        String accessToken = jwtUtil.generateAccessToken(authentication.getName()); // AccessToken 생성!
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName()); // RefreshToken 생성!

        return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
    }

    @GetMapping("/member/mypage")
    public ResponseEntity<?> mypage(){
        log.debug("회원의 마이페이지 접근 성공!");
        return ResponseEntity.ok("접근 성공!");
    }
}
