package com.wedit.backend.api.member.repository;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByCoupleCode(String coupleCode);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Couple c WHERE c.groom = :member OR c.bride = :member")
    Optional<Couple> findByGroomOrBrideWithLock(@Param("member") Member member);

    @Query("SELECT c FROM Couple c WHERE c.groom = :member OR c.bride = :member")
    Optional<Couple> findByGroomOrBride(@Param("member") Member member);
}
