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
		
		log.info("초청장 생성 시작 - memberEmail: {}", memberEmail);
		
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// Pessimistic Lock으로 다른 트랜잭션 대기걸어줌
		Optional<Couple> couple = coupleRepository.findByGroomOrBrideWithLock(member);

		if (invitationRepository.existsByMember(member)) {
			log.warn("초청장 생성 실패 - 이미 초청장 보유 중. memberId: {}", member.getId());
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_HAVE_INVITATION.getMessage());
		}

		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			if (invitationRepository.existsByMember(otherMember)) {
				log.warn("초청장 생성 실패 - 커플의 다른 멤버가 이미 보유 중. memberId: {}, otherMemberId: {}", 
						member.getId(), otherMember.getId());
				throw new BadRequestException(
					ErrorStatus.BAD_REQUEST_ALREADY_OTHER_MEMBER_HAVE_INVITATION.getMessage());
			}
		}

		try {
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
			log.debug("초청장 기본 정보 저장 완료 - invitationId: {}", saved.getId());

			// 메인 사진 저장
			if (createRequestDTO.getMainMedia() != null) {
				Media main = createRequestDTO.getMainMedia().toEntity(MediaDomain.INVITATION, saved.getId(), "main");
				mediaService.save(main);
				log.debug("메인 미디어 저장 완료 - invitationId: {}", saved.getId());
			}

			// 필름 사진 저장
			if (createRequestDTO.getFilmMedia() != null && !createRequestDTO.getFilmMedia().isEmpty()) {
				List<Media> mediaToSave = createRequestDTO.getFilmMedia().stream()
					.map(mediaDto -> mediaDto.toEntity(MediaDomain.INVITATION, saved.getId(), "film"))
					.collect(Collectors.toList());
				mediaService.saveAll(mediaToSave);
				log.debug("필름 미디어 저장 완료 - invitationId: {}, 개수: {}", saved.getId(), mediaToSave.size());
			}

			// 티켓 사진 저장
			if (createRequestDTO.getTicketMedia() != null) {
				Media main = createRequestDTO.getTicketMedia().toEntity(MediaDomain.INVITATION, saved.getId(), "ticket");
				mediaService.save(main);
				log.debug("티켓 미디어 저장 완료 - invitationId: {}", saved.getId());
			}

			if (createRequestDTO.getMediaList() != null && !createRequestDTO.getMediaList().isEmpty()) {
				List<Media> mediaToSave = createRequestDTO.getMediaList().stream()
					.map(mediaDto -> mediaDto.toEntity(MediaDomain.INVITATION, saved.getId(), "media"))
					.collect(Collectors.toList());

				mediaService.saveAll(mediaToSave);
				log.debug("일반 미디어 저장 완료 - invitationId: {}, 개수: {}", saved.getId(), mediaToSave.size());
			}
			
			log.info("초청장 생성 완료 - memberId: {}, invitationId: {}", member.getId(), saved.getId());
		} catch (Exception e) {
			log.error("초청장 생성 실패 - memberEmail: {}", memberEmail, e);
			throw e;
		}
	}

	public InvitationGetResponseDTO getInvitation(String memberEmail) {
		
		log.debug("초청장 조회 시작 - memberEmail: {}", memberEmail);
		
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Optional<Couple> couple = coupleRepository.findByGroomOrBride(member);

		// 본인의 초청장 먼저 확인
		Optional<Invitation> invitation = invitationRepository.findByMember(member);
		if (invitation.isPresent()) {
			log.debug("본인 초청장 발견 - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());
			
			InvitationGetResponseDTO response = buildInvitationResponse(invitation.get());
			log.info("초청장 조회 완료 (본인) - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());
			return response;
		}

		// 커플의 다른 멤버 초청장 확인
		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			invitation = invitationRepository.findByMember(otherMember);
			if (invitation.isPresent()) {
				log.debug("커플 상대방 초청장 발견 - memberId: {}, otherMemberId: {}, invitationId: {}", 
						member.getId(), otherMember.getId(), invitation.get().getId());
				
				InvitationGetResponseDTO response = buildInvitationResponse(invitation.get());
				log.info("초청장 조회 완료 (커플) - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());
				return response;
			}
		}
		
		log.info("초청장 없음 - memberEmail: {}", memberEmail);
		return null;
	}
	
	private InvitationGetResponseDTO buildInvitationResponse(Invitation invitation) {
		try {
			String mainMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(), "main")
				.getFirst();

			List<String> filmMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(),
				"film");

			String ticketMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(),
					"ticket")
				.getFirst();

			List<String> mediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(),
				"media");

			return InvitationGetResponseDTO.builder()
				.id(invitation.getId())
				.basicInformation(invitation.getBasicInformation())
				.greetings(invitation.getGreetings())
				.marriageDate(invitation.getMarriageDate())
				.marriagePlace(invitation.getMarriagePlace())
				.gallery(invitation.getGallery())
				.ending(invitation.getEnding())
				.account(invitation.getAccount())
				.background(invitation.getBackground())
				.memberId(invitation.getMember().getId())
				.mainMediaUrl(mainMediaUrl)
				.filmMediaUrl(filmMediaUrl)
				.ticketMediaUrl(ticketMediaUrl)
				.mediaUrls(mediaUrl)
				.build();
		} catch (Exception e) {
			log.error("초청장 응답 생성 실패 - invitationId: {}", invitation.getId(), e);
			throw e;
		}
	}
}
