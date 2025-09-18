package com.wedit.backend.api.invitation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationMyPageResponseDTO {
	
	private boolean hasInvitation;
	private String mainMediaUrl;
	private String source;
	private Long invitationId;
	
	// 편의 생성자들
	public static InvitationMyPageResponseDTO noInvitation() {
		return InvitationMyPageResponseDTO.builder()
			.hasInvitation(false)
			.mainMediaUrl(null)
			.source(null)
			.invitationId(null)
			.build();
	}
	
	public static InvitationMyPageResponseDTO withOwnInvitation(Long invitationId, String mainMediaUrl) {
		return InvitationMyPageResponseDTO.builder()
			.hasInvitation(true)
			.mainMediaUrl(mainMediaUrl)
			.source("own")
			.invitationId(invitationId)
			.build();
	}
	
	public static InvitationMyPageResponseDTO withCoupleInvitation(Long invitationId, String mainMediaUrl) {
		return InvitationMyPageResponseDTO.builder()
			.hasInvitation(true)
			.mainMediaUrl(mainMediaUrl)
			.source("couple")
			.invitationId(invitationId)
			.build();
	}
}
