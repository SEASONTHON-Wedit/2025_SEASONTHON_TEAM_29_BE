package com.wedit.backend.api.invitation.dto;

import java.util.List;

import com.wedit.backend.api.invitation.entity.Account;
import com.wedit.backend.api.invitation.entity.Background;
import com.wedit.backend.api.invitation.entity.BasicInformation;
import com.wedit.backend.api.invitation.entity.Ending;
import com.wedit.backend.api.invitation.entity.Gallery;
import com.wedit.backend.api.invitation.entity.Greetings;
import com.wedit.backend.api.invitation.entity.MarriageDate;
import com.wedit.backend.api.invitation.entity.MarriagePlace;
import com.wedit.backend.api.invitation.entity.Theme;
import com.wedit.backend.api.member.entity.Member;

import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private Ending ending;
	private Account account;
	private Background background;
	private Long memberId;

	private String mainMediaUrl;
	private List<String> filmMediaUrl;
	private String ticketMediaUrl;

	private List<String> mediaUrls;
}
