package com.ssg.jwtlogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class MemberController {
    @PostMapping("/member/login")
    public ResponseEntity<?> login() {
        log.debug("로그인 요청 처리를 개발자가 정의한 컨트롤러에서 처리함");
        return null;
    }
}
