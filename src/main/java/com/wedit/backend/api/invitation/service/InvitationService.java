package com.wedit.backend.api.invitation.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.invitation.dto.InvitationCreateRequestDTO;
import com.wedit.backend.api.invitation.dto.InvitationGetResponseDTO;
import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.invitation.entity.MarriagePlace;
import com.wedit.backend.api.invitation.repository.InvitationRepository;
import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
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
	private final VendorRepository vendorRepository;

	@Transactional
	public void createInvitation(String memberEmail, InvitationCreateRequestDTO createRequestDTO) {

		log.info("청첩장 생성 시작 - memberEmail: {}", memberEmail);

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// Pessimistic Lock으로 다른 트랜잭션 대기걸어줌
		Optional<Couple> couple = coupleRepository.findByGroomOrBrideWithLock(member);

		if (invitationRepository.existsByMember(member)) {
			log.warn("청첩장 생성 실패 - 이미 청첩장 보유 중. memberId: {}", member.getId());
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_HAVE_INVITATION.getMessage());
		}

		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			if (invitationRepository.existsByMember(otherMember)) {
				log.warn("청첩장 생성 실패 - 커플의 다른 멤버가 이미 보유 중. memberId: {}, otherMemberId: {}",
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
				.member(member)
				.build();

			Invitation saved = invitationRepository.save(invitation);
			log.debug("청첩장 기본 정보 저장 완료 - invitationId: {}", saved.getId());

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
				Media main = createRequestDTO.getTicketMedia()
					.toEntity(MediaDomain.INVITATION, saved.getId(), "ticket");
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

			log.info("청첩장 생성 완료 - memberId: {}, invitationId: {}", member.getId(), saved.getId());
		} catch (Exception e) {
			log.error("청첩장 생성 실패 - memberEmail: {}", memberEmail, e);
			throw e;
		}
	}

	public InvitationGetResponseDTO getInvitation(String memberEmail) {

		log.debug("청첩장 조회 시작 - memberEmail: {}", memberEmail);

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Optional<Couple> couple = coupleRepository.findByGroomOrBride(member);

		// 본인의 청첩장 먼저 확인
		Optional<Invitation> invitation = invitationRepository.findByMember(member);
		if (invitation.isPresent()) {
			log.debug("본인 청첩장 발견 - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());

			InvitationGetResponseDTO response = buildInvitationResponse(invitation.get());
			log.info("청첩장 조회 완료 (본인) - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());
			return response;
		}

		// 커플의 다른 멤버 청첩장 확인
		if (couple.isPresent()) {
			Member otherMember = couple.get().getOtherMember(member);
			invitation = invitationRepository.findByMember(otherMember);
			if (invitation.isPresent()) {
				log.debug("커플 상대방 청첩장 발견 - memberId: {}, otherMemberId: {}, invitationId: {}",
					member.getId(), otherMember.getId(), invitation.get().getId());

				InvitationGetResponseDTO response = buildInvitationResponse(invitation.get());
				log.info("청첩장 조회 완료 (커플) - memberId: {}, invitationId: {}", member.getId(), invitation.get().getId());
				return response;
			}
		}

		log.info("청첩장 없음 - memberEmail: {}", memberEmail);
		return null;
	}

	private InvitationGetResponseDTO buildInvitationResponse(Invitation invitation) {
		try {
			// 메인 미디어 URL 조회 (안전하게 처리)
			List<String> mainMediaUrls = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(), "main");
			String mainMediaUrl = mainMediaUrls.isEmpty() ? null : mainMediaUrls.get(0);

			// 필름 미디어 URL 리스트 조회
			List<String> filmMediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(), "film");

			// 티켓 미디어 URL 조회 (안전하게 처리)
			List<String> ticketMediaUrls = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(),
				"ticket");
			String ticketMediaUrl = ticketMediaUrls.isEmpty() ? null : ticketMediaUrls.get(0);

			// 일반 미디어 URL 리스트 조회
			List<String> mediaUrl = mediaService.findMediaUrls(MediaDomain.INVITATION, invitation.getId(), "media");

			// MarriagePlace에 location 정보 설정
			MarriagePlace marriagePlace = invitation.getMarriagePlace();
			if (marriagePlace != null && marriagePlace.getVendorName() != null && !marriagePlace.getVendorName()
				.trim()
				.isEmpty()) {
				// 업체명을 통해 업체 조회하여 full address 설정
				Optional<Vendor> vendorOpt = vendorRepository.findFirstByName(marriagePlace.getVendorName());
				if (vendorOpt.isPresent()) {
					Vendor vendor = vendorOpt.get();
					String location = vendor.getFullAddress();
					if (vendor.getAddressDetail() != null && !vendor.getAddressDetail().trim().isEmpty()) {
						location += " " + vendor.getAddressDetail();
					}
					marriagePlace.setLocation(location);
					log.debug("결혼식장 위치 정보 설정 완료 - vendorName: {}, location: {}",
						marriagePlace.getVendorName(), location);
				} else {
					log.warn("결혼식장 업체를 찾을 수 없음 - vendorName: {}", marriagePlace.getVendorName());
				}
			}

			return InvitationGetResponseDTO.builder()
				.id(invitation.getId())
				.basicInformation(invitation.getBasicInformation())
				.greetings(invitation.getGreetings())
				.marriageDate(invitation.getMarriageDate())
				.marriagePlace(marriagePlace)
				.gallery(invitation.getGallery())
				.memberId(invitation.getMember().getId())
				.mainMediaUrl(mainMediaUrl)
				.filmMediaUrl(filmMediaUrl)
				.ticketMediaUrl(ticketMediaUrl)
				.mediaUrls(mediaUrl)
				.build();
		} catch (Exception e) {
			log.error("청첩장 응답 생성 실패 - invitationId: {}", invitation.getId(), e);
			throw e;
		}
	}
}
