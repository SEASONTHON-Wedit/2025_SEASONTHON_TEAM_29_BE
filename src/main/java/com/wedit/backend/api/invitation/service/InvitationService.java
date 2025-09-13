package com.wedit.backend.api.invitation.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.invitation.dto.InvitationCreateRequestDTO;
import com.wedit.backend.api.invitation.dto.InvitationGetResponseDTO;
import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.invitation.repository.InvitationRepository;
import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
	private final InvitationRepository invitationRepository;
	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;
	private final MediaService mediaService;

	@Transactional
	public void createInvitation(String memberEmail, InvitationCreateRequestDTO createRequestDTO) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// Pessimistic Lock으로 다른 트랜잭션 대기걸어줌
		Optional<Couple> couple = coupleRepository.findByGroomOrBrideWithLock(member);

		if (invitationRepository.existsByMember(member)) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_HAVE_INVITATION.getMessage());
		}

		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			if (invitationRepository.existsByMember(otherMember)) {
				throw new BadRequestException(
					ErrorStatus.BAD_REQUEST_ALREADY_OTHER_MEMBER_HAVE_INVITATION.getMessage());
			}
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

		Invitation saved = invitationRepository.save(invitation);

		// 메인 사진 저장
		if (createRequestDTO.getMainMedia() != null) {
			Media main = createRequestDTO.getMainMedia().toEntity(MediaDomain.INVITATION, saved.getId(), "main");
			mediaService.save(main);
		}

		// 필름 사진 저장
		if (createRequestDTO.getFilmMedia() != null && !createRequestDTO.getFilmMedia().isEmpty()) {
			List<Media> mediaToSave = createRequestDTO.getFilmMedia().stream()
				.map(mediaDto -> mediaDto.toEntity(MediaDomain.INVITATION, saved.getId(), "film"))
				.collect(Collectors.toList());
			mediaService.saveAll(mediaToSave);
		}

		// 티켓 사진 저장
		if (createRequestDTO.getTicketMedia() != null) {
			Media main = createRequestDTO.getTicketMedia().toEntity(MediaDomain.INVITATION, saved.getId(), "ticket");
			mediaService.save(main);
		}

		if (createRequestDTO.getMediaList() != null && !createRequestDTO.getMediaList().isEmpty()) {
			List<Media> mediaToSave = createRequestDTO.getMediaList().stream()
				.map(mediaDto -> mediaDto.toEntity(MediaDomain.INVITATION, saved.getId(), "media"))
				.collect(Collectors.toList());

			mediaService.saveAll(mediaToSave);
		}
	}

	public InvitationGetResponseDTO getInvitation(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Optional<Couple> couple = coupleRepository.findByGroomOrBride(member);

		Optional<Invitation> invitation = invitationRepository.findByMember(member);
		if (invitation.isPresent()) {
			String mainMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(), "main")
				.getFirst();

			List<String> filmMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
				"film");

			String ticketMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
					"ticket")
				.getFirst();

			List<String> mediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
				"media");

			return InvitationGetResponseDTO.builder()
				.id(invitation.get().getId())
				.basicInformation(invitation.get().getBasicInformation())
				.greetings(invitation.get().getGreetings())
				.marriageDate(invitation.get().getMarriageDate())
				.marriagePlace(invitation.get().getMarriagePlace())
				.gallery(invitation.get().getGallery())
				.ending(invitation.get().getEnding())
				.account(invitation.get().getAccount())
				.background(invitation.get().getBackground())
				.memberId(invitation.get().getMember().getId())
				.mainMediaUrl(mainMediaUrl)
				.filmMediaUrl(filmMediaUrl)
				.ticketMediaUrl(ticketMediaUrl)
				.mediaUrls(mediaUrl)
				.build();
		}

		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			invitation = invitationRepository.findByMember(otherMember);
			if (invitation.isPresent()) {
				String mainMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(), "main")
					.getFirst();

				List<String> filmMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
					"film");

				String ticketMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
						"ticket")
					.getFirst();

				List<String> mediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.get().getId(),
					"media");

				return InvitationGetResponseDTO.builder()
					.id(invitation.get().getId())
					.basicInformation(invitation.get().getBasicInformation())
					.greetings(invitation.get().getGreetings())
					.marriageDate(invitation.get().getMarriageDate())
					.marriagePlace(invitation.get().getMarriagePlace())
					.gallery(invitation.get().getGallery())
					.ending(invitation.get().getEnding())
					.account(invitation.get().getAccount())
					.background(invitation.get().getBackground())
					.memberId(invitation.get().getMember().getId())
					.mainMediaUrl(mainMediaUrl)
					.filmMediaUrl(filmMediaUrl)
					.ticketMediaUrl(ticketMediaUrl)
					.mediaUrls(mediaUrl)
					.build();
			}
		}
		return null;
	}
}
