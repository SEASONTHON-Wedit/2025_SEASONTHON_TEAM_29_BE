package com.wedit.backend.api.member.repository;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByCoupleCode(String coupleCode);

    // 신랑 또는 신부 필드에 특정 회원이 포함된 Couple 엔티티 찾기
    @Query("SELECT c FROM Couple c WHERE c.groom = :member OR c.bride = :member")
    Optional<Couple> findByGroomOrBride(@Param("member") Member member);
}
