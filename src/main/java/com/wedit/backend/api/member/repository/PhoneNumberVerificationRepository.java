package com.wedit.backend.api.member.repository;

import com.wedit.backend.api.member.entity.PhoneNumberVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneNumberVerificationRepository extends JpaRepository<PhoneNumberVerification, Long> {

    Optional<PhoneNumberVerification> findByPhoneNumber(String phoneNumber);

    Optional<PhoneNumberVerification> findByPhoneNumberAndCode(String phoneNumber, String code);
}
