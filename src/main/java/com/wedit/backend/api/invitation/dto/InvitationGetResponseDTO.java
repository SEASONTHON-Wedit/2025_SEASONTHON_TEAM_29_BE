package com.wedit.backend.api.invitation.dto;

import java.util.List;

import com.wedit.backend.api.invitation.entity.BasicInformation;
import com.wedit.backend.api.invitation.entity.Gallery;
import com.wedit.backend.api.invitation.entity.Greetings;
import com.wedit.backend.api.invitation.entity.MarriageDate;
import com.wedit.backend.api.invitation.entity.MarriagePlace;
import com.wedit.backend.api.invitation.entity.Theme;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationGetResponseDTO {
	private Long id;
	private Theme theme;
	private BasicInformation basicInformation;
	private Greetings greetings;
	private MarriageDate marriageDate;
	private MarriagePlace marriagePlace;
	private Gallery gallery;
	private Long memberId;

	private String mainMediaUrl;
	private List<String> filmMediaUrl;
	private String ticketMediaUrl;

	private List<String> mediaUrls;
}
