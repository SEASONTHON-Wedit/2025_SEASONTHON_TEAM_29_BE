package com.wedit.backend.api.invitation.service;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.invitation.dto.InvitationCreateRequestDTO;
import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.invitation.repository.InvitationRepository;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
	private final InvitationRepository invitationRepository;
	private final MemberRepository memberRepository;

	public void createInvitation(String memberEmail, InvitationCreateRequestDTO createRequestDTO) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		if (invitationRepository.existsByMember(member)) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_HAVE_INVITATION.getMessage());
		}

		Invitation invitation = Invitation.builder()
			.theme(createRequestDTO.getTheme())
			.basicInformation(createRequestDTO.getBasicInformation())
			.greetings(createRequestDTO.getGreetings())
			.marriageDate(createRequestDTO.getMarriageDate())
			.marriagePlace(createRequestDTO.getMarriagePlace())
			.gallery(createRequestDTO.getGallery())
			.ending(createRequestDTO.getEnding())
			.account(createRequestDTO.getAccount())
			.background(createRequestDTO.getBackground())
			.member(member)
			.build();

		invitationRepository.save(invitation);

	}
}
