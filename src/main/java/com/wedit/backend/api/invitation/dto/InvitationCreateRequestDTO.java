package com.wedit.backend.api.invitation.dto;

import com.wedit.backend.api.invitation.entity.Account;
import com.wedit.backend.api.invitation.entity.Background;
import com.wedit.backend.api.invitation.entity.BasicInformation;
import com.wedit.backend.api.invitation.entity.Ending;
import com.wedit.backend.api.invitation.entity.Gallery;
import com.wedit.backend.api.invitation.entity.Greetings;
import com.wedit.backend.api.invitation.entity.MarriageDate;
import com.wedit.backend.api.invitation.entity.MarriagePlace;
import com.wedit.backend.api.invitation.entity.Theme;

import lombok.Data;

@Data
public class InvitationCreateRequestDTO {
	private Theme theme;
	private BasicInformation basicInformation;
	private Greetings greetings;
	private MarriageDate marriageDate;
	private MarriagePlace marriagePlace;
	private Gallery gallery;
	private Ending ending;
	private Account account;
	private Background background;
}
