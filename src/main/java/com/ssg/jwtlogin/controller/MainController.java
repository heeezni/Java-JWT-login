package com.ssg.jwtlogin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    // Spring Security 기본으로 제공되는 폼 로그인은,
    // 로그인 인증 성공 후 무조건 /로 리다이렉트 -> /에 대한 요청 처리하자
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
