package com.ssg.jwtlogin.model.member;

import com.ssg.jwtlogin.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 비밀번호 검증은 시큐리티가 알아서 해주므로, 개발자는 id를 이용한 회원정보 가져오기 메서드만 정의하면됨
 * */
public interface JpaMemberRepository extends JpaRepository<Member, Integer> {
    Member findById(String id);
}
