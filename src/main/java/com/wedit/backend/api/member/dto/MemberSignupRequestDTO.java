package com.wedit.backend.api.member.dto;

import com.wedit.backend.api.member.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupRequestDTO {

    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    private LocalDate birthDate;
    private LocalDate weddingDate;

    private Type type;
}
