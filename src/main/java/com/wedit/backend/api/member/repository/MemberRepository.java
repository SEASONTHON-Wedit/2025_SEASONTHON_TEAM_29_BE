package com.wedit.backend.api.member.repository;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // id로 Member 조회
    Optional<Member> findById(Long id);

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 전화번호로 회원 조회
    Optional<Member> findByPhoneNumber(String phoneNumber);

    // OAuth ID로 회원 조회
    Optional<Member> findByOauthId(String oauthId);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 전화번호 존재 여부 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // 특정 상태의 회원 조회
    Optional<Member> findByIdAndType(Long id, Type status);
}
