package com.ssg.jwtlogin.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 알고리즘을 적용한 비밀번호 생성 클래스
 * */
public class PasswordGenerator {
    /**
     * @param password 이 평문 비밀번호를 BCrypt 알고리즘을 적용하여 변환함
     * */
    public static String convert(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String result = encoder.encode(password);

        return result;
    }

    public static void main(String[] args) {
        // current File에서 Run 하기
        System.out.println(convert("user"));
    }
}
