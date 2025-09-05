# 프로젝트 흐름 설명

이 문서는 `JwtLogin` 프로젝트의 주요 클래스별 역할과 내부 메서드들의 흐름을 상세히 설명합니다.

## 1. `JwtLoginApplication.java`

*   **역할**: Spring Boot 애플리케이션의 메인 진입점입니다. 애플리케이션을 실행하고 Spring 컨텍스트를 로드하는 역할을 합니다.
*   **주요 메서드**:
    *   `main(String[] args)`: 애플리케이션을 시작하는 표준 Java 메인 메서드입니다. `SpringApplication.run()`을 호출하여 Spring Boot 애플리케이션을 부트스트랩합니다.

## 2. `controller/MainController.java`

*   **역할**: 애플리케이션의 메인 페이지(`index`)를 제공하는 컨트롤러입니다. Spring Security의 기본 로그인 성공 후 리다이렉트 경로를 처리합니다.
*   **주요 메서드**:
    *   `index()`: HTTP GET 요청으로 `/` 경로에 접근했을 때 `index` 뷰(HTML 파일 등)를 반환합니다.

## 3. `controller/MemberController.java`

*   **역할**: 회원 관련 요청, 특히 로그인 처리를 담당하는 컨트롤러입니다. Spring Security의 인증 흐름과 연동됩니다.
*   **주요 메서드**:
    *   `login(@RequestParam("id") String username, @RequestParam("pwd") String password)`: HTTP POST 요청으로 `/member/login` 경로에 접근했을 때 호출됩니다.
        1.  `UsernamePasswordAuthenticationToken`을 생성하여 사용자 이름과 비밀번호를 캡슐화합니다.
        2.  `AuthenticationManager`의 `authenticate()` 메서드를 호출하여 실제 인증을 시도합니다. 이 과정에서 `DaoAuthenticationProvider`가 동작하여 `CustomUserDetailsService`를 통해 사용자 정보를 로드하고 비밀번호를 검증합니다.
        3.  인증 성공 시, `JwtUtil`을 사용하여 인증된 사용자 정보(`authentication.getName()`)를 기반으로 Access Token과 Refresh Token을 생성합니다.
        4.  생성된 토큰들을 `ResponseEntity`에 담아 클라이언트에게 반환합니다.
    *   `mypage()`: HTTP GET 요청으로 `/member/mypage` 경로에 접근했을 때 호출됩니다. 인증된 사용자의 마이페이지 접근을 처리합니다.

## 4. `domain/Member.java`

*   **역할**: 데이터베이스의 `member` 테이블과 매핑되는 JPA 엔티티 클래스입니다. 회원의 정보를 담는 도메인 객체입니다.
*   **주요 필드**:
    *   `memberId`: 회원 고유 ID (Primary Key, 자동 생성)
    *   `id`: 회원 로그인 ID
    *   `pwd`: 회원 비밀번호
    *   `name`: 회원 이름

## 5. `domain/CustomUserDetails.java`

*   **역할**: Spring Security가 사용자 정보를 처리할 수 있도록 `UserDetails` 인터페이스를 구현한 클래스입니다. 실제 `Member` 객체를 래핑하여 Spring Security의 인증 및 권한 부여 과정에서 사용됩니다.
*   **주요 메서드**:
    *   `getAuthorities()`: 사용자의 권한 목록을 반환합니다. 현재는 빈 리스트를 반환합니다. (역할/권한 관리가 필요할 경우 이 메서드를 통해 구현할 수 있습니다.)
    *   `getPassword()`: 사용자의 비밀번호를 반환합니다 (`Member` 객체의 `pwd` 필드).
    *   `getUsername()`: 사용자의 사용자 이름(로그인 ID)을 반환합니다 (`Member` 객체의 `id` 필드).

## 6. `model/member/JpaMemberRepository.java`

*   **역할**: `Member` 엔티티에 대한 데이터베이스 접근을 담당하는 Spring Data JPA 리포지토리 인터페이스입니다. Spring Data JPA의 기능을 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 작업을 수행할 수 있으며, 사용자 정의 쿼리 메서드를 정의할 수 있습니다.
*   **주요 메서드**:
    *   `findById(String id)`: 주어진 로그인 ID(String `id`)를 사용하여 데이터베이스에서 `Member` 객체를 조회합니다. Spring Security의 `UserDetailsService`에서 사용자 정보를 로드할 때 사용됩니다.

## 7. `model/member/CustomUserDetailsService.java`

*   **역할**: Spring Security의 `UserDetailsService` 인터페이스를 구현하여, 사용자 로그인 시 데이터베이스에서 사용자 정보를 로드하는 서비스입니다. `DaoAuthenticationProvider`가 사용자 인증을 수행할 때 이 서비스를 사용합니다.
*   **주요 메서드**:
    *   `loadUserByUsername(String username)`: 주어진 사용자 이름(로그인 ID)으로 데이터베이스에서 `Member` 객체를 조회합니다. 해당 사용자가 없으면 `UsernameNotFoundException`을 발생시키고, 찾으면 `CustomUserDetails` 객체로 변환하여 반환합니다.

## 8. `security/JwtSecurityConfig.java`

*   **역할**: JWT 기반 인증을 위한 Spring Security의 핵심 설정 클래스입니다. `@Configuration`과 `@EnableWebSecurity` 어노테이션을 통해 보안 설정을 정의합니다. 세션 기반 인증을 비활성화하고 무상태(stateless) 인증을 강제합니다.
*   **주요 메서드 및 빈**:
    *   `jwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService)`: `JwtUtil`과 `CustomUserDetailsService`를 주입받아 JWT 토큰의 유효성을 검증하고 인증을 처리하는 `JwtAuthFilter` 빈을 생성합니다. 이 필터는 Spring Security 필터 체인에 추가되어 모든 요청에 대해 JWT를 검사합니다.
    *   `passwordEncoder()`: 비밀번호 암호화를 위한 `BCryptPasswordEncoder` 빈을 제공합니다.
    *   `daoAuthenticationProvider()`: `CustomUserDetailsService`와 `PasswordEncoder`를 사용하여 사용자 인증을 수행하는 `DaoAuthenticationProvider` 빈을 설정합니다. (참고: 이 빈을 명시적으로 정의하면 Spring Security의 기본 `UserDetailsService` 자동 구성이 우회됩니다. 이는 의도적인 설정입니다.)
    *   `authenticationManager(AuthenticationConfiguration configuration)`: Spring Security의 인증 관리자(`AuthenticationManager`) 빈을 제공합니다. 이 관리자는 실제 인증 과정을 위임합니다.
    *   `customSecurityFilterChain(HttpSecurity http, OncePerRequestFilter jwtAuthFilter, DaoAuthenticationProvider provider)`: Spring Security의 필터 체인을 구성합니다. CSRF 비활성화, 세션 생성 정책을 `STATELESS`로 설정, 폼 로그인 비활성화 등을 수행합니다.
        *   `authorizeHttpRequests`: `/member/login.html` 및 `/member/login` 경로에 대한 접근을 허용하고, 그 외 모든 요청에 대해 인증을 요구하도록 설정합니다.
        *   `addFilterBefore(jwtAuthFilter, AuthorizationFilter.class)`: `JwtAuthFilter`를 `AuthorizationFilter` 앞에 추가하여 모든 요청에 대해 JWT 인증이 먼저 이루어지도록 합니다.

## 9. `security/FormLoginSecurityConfig.java`

*   **역할**: (현재 비활성화됨) 전통적인 폼 로그인 기반의 Spring Security 설정을 정의하는 클래스입니다. `JwtSecurityConfig`와 유사하게 보안 관련 빈들을 정의하지만, 세션 기반의 폼 로그인 방식을 사용합니다. 현재 `@Configuration` 및 `@EnableWebSecurity` 어노테이션이 주석 처리되어 있어 애플리케이션에 로드되지 않습니다.
*   **주요 메서드 및 빈**:
    *   `passwordEncoder()`: 비밀번호 암호화를 위한 `BCryptPasswordEncoder` 빈을 제공합니다.
    *   `authenticationProvider()`: `CustomUserDetailsService`와 `PasswordEncoder`를 사용하여 사용자 인증을 수행하는 `DaoAuthenticationProvider` 빈을 설정합니다.
    *   `authenticationManager()`: Spring Security의 인증 관리자(`AuthenticationManager`) 빈을 제공합니다.
    *   `securityFilterChain()`: Spring Security의 필터 체인을 구성하며, 폼 로그인 관련 설정을 포함합니다.

## 10. `util/JwtUtil.java`

*   **역할**: JWT(JSON Web Token)의 생성, 유효성 검증, 그리고 토큰에서 사용자 정보를 추출하는 유틸리티 클래스입니다. `application.properties`에 정의된 비밀 키와 만료 시간을 사용합니다.
*   **주요 메서드**:
    *   `generateAccessToken(String username)`: 주어진 사용자 이름으로 Access Token을 생성합니다.
    *   `generateRefreshToken(String username)`: 주어진 사용자 이름으로 Refresh Token을 생성합니다.
    *   `generateToken(String username, long expiration)`: 실제 토큰을 생성하는 내부 메서드입니다. 사용자 이름, 발급 시간, 만료 시간, 서명 알고리즘 등을 포함합니다.
    *   `validateToken(String token)`: 주어진 JWT 토큰의 유효성(서명 일치, 만료 여부)을 검증합니다.
    *   `getUsername(String token)`: 유효한 JWT 토큰에서 사용자 이름(subject)을 추출합니다.

## 11. `filter/JwtAuthFilter.java`

*   **역할**: 클라이언트의 모든 요청에 대해 JWT 토큰의 유효성을 검증하는 커스텀 Spring Security 필터입니다. `OncePerRequestFilter`를 상속받아 각 요청당 한 번만 실행되도록 보장합니다.
*   **주요 메서드**:
    *   `doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`: 실제 필터링 로직이 구현된 메서드입니다. HTTP 요청 헤더에서 `Authorization` 헤더를 찾아 `Bearer` 토큰을 추출합니다. 추출된 토큰은 `JwtUtil`을 통해 유효성이 검증되며, 유효한 경우 `CustomUserDetailsService`를 통해 사용자 정보를 로드하고 `SecurityContextHolder`에 `UsernamePasswordAuthenticationToken`을 설정하여 인증 정보를 저장합니다. 또한, 토큰이 유효하지 않을 경우 401 Unauthorized 응답을 반환하는 에러 처리 로직이 추가되었습니다. (참고: `SecurityContextHolder.getContext().getAuthentication() == null` 검사는 제거되어, 필터가 항상 토큰을 처리하도록 변경되었습니다.)