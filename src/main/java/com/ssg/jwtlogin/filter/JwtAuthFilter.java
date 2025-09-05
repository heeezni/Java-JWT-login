package com.ssg.jwtlogin.filter;

import com.ssg.jwtlogin.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 클라이언트의 요청을 처리하는 모든 컨트롤러에서 각각 JWT 유효성 체크하기보다는
 * 요청 입구에서 동작하는 필터차원에서 JWT의 유효성을 체크해보자!
 * ( 단, JavaEE의 일반 필터 x, Spring Security에서 지원되는 필터를 개발자가 커스텀해야함 )
 * OncePerRequestFilter : 하나의 요청마다 단 한 번만 실행되는 필터 (같은 요청에 대해서 이 필터가 여러번 호출되지 않도록 보장)
 * JWT 토큰 검증 시, 검증은 한 번만 실행되면 충분한 작업이기때문
 *
 * 선택사항이지만, 필터를 사용하지 않을 경우, 모든 컨트롤러 메서드 마다 회원인증이 필요한 서비스의 경우 JWT인증 코드가 중복됨
 * */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("OncePerRequestFilter로 구현한 검증필터 동작");

        // 클라이언트가 Authorization의 Bearer에 함께 보낸 Token을 검증
        String header = request.getHeader("Authorization");
        if (header != null
                && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7); // index 7번째부터 토큰이 시작
            if(jwtUtil.validateToken(token)){  // 토큰 검증 시도 -> 토큰이 유효하다면?
                // 사용자 정보 중 username 추출
                String username = jwtUtil.getUsername(token);
                log.debug("토큰으로 부터 추출한 사용자 정보는 {}",username);
            }
        }

        filterChain.doFilter(request,response);
    }
}
