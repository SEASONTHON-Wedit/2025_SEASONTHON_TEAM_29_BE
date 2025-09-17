package com.wedit.backend.api.invitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;

import jakarta.persistence.LockModeType;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
	boolean existsByMember(Member member);

	Optional<Invitation> findByMember(Member member);
}
