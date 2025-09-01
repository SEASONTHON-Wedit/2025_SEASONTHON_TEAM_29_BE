package com.wedit.backend.api.member.dto;

import java.time.LocalDate;

import com.wedit.backend.api.member.entity.Type;

import lombok.Data;

@Data
public class SocialMemberAdditionalRequestDTO {
	private LocalDate birthDate;
	private String phoneNumber;
	private LocalDate weddingDate;
	private Type type;
}
