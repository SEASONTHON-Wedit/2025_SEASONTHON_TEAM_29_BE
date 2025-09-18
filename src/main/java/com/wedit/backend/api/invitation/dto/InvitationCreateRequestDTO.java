package com.wedit.backend.api.invitation.dto;

import java.util.List;

import com.wedit.backend.api.invitation.entity.BasicInformation;
import com.wedit.backend.api.invitation.entity.Gallery;
import com.wedit.backend.api.invitation.entity.Greetings;
import com.wedit.backend.api.invitation.entity.MarriageDate;
import com.wedit.backend.api.invitation.entity.MarriagePlace;
import com.wedit.backend.api.invitation.entity.Theme;
import com.wedit.backend.api.media.dto.MediaRequestDTO;

import lombok.Data;

@Data
public class InvitationCreateRequestDTO {
	private Theme theme;
	private BasicInformation basicInformation;
	private Greetings greetings;
	private MarriageDate marriageDate;
	private MarriagePlace marriagePlace;
	private Gallery gallery;

	private MediaRequestDTO mainMedia;
	private List<MediaRequestDTO> filmMedia;
	private MediaRequestDTO ticketMedia;

	private List<MediaRequestDTO> mediaList;
}
