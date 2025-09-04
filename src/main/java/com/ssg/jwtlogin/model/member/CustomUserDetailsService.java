package com.ssg.jwtlogin.model.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서는 개발자가 로그인 검증을 위한 서비스 객체를 별도로 정의할 필요X
 * UserDetailsService를 구현하면 됨
 * */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 해당 유저명으로 객체를 조회

        // 코드에는 보이지 않지만, 내부적으로 DaoAuthenticationProvider가 비밀번호 검증을 스스로 수행
        return null;
    }
}
