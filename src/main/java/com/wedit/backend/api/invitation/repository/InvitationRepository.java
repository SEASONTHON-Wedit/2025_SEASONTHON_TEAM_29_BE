package com.wedit.backend.api.invitation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.member.entity.Member;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
	boolean existsByMember(Member member);
}
