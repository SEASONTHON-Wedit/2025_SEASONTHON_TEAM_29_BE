package com.wedit.backend.api.invitation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.invitation.dto.InvitationCreateRequestDTO;
import com.wedit.backend.api.invitation.dto.InvitationGetResponseDTO;
import com.wedit.backend.api.invitation.entity.Invitation;
import com.wedit.backend.api.invitation.service.InvitationService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/invitation")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Invitation", description = "Invitation 관련 API 입니다.")
public class InvitationController {
	private final InvitationService invitationService;

	@Operation(
		summary = "초청장 생성 API", description = "현재는 ending, account, background는 없어서 빼고 보내주시면 됩니다!"
	)
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createInvitation(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody InvitationCreateRequestDTO createRequestDTO) {
		invitationService.createInvitation(userDetails.getUsername(), createRequestDTO);
		return ApiResponse.successOnly(SuccessStatus.INVITATION_CREATE_SUCCESS);
	}

	@Operation(
		summary = "초청장 조회 API", description = "초청정 조회 API"
	)
	@GetMapping
	public ResponseEntity<ApiResponse<InvitationGetResponseDTO>> getInvitation(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		InvitationGetResponseDTO invitation = invitationService.getInvitation(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.AUTH_SUCCESS, invitation);
	}
}
