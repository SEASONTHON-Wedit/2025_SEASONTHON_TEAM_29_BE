package com.wedit.backend.api.member.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wedit.backend.api.member.dto.MemberLoginRequestDTO;
import com.wedit.backend.api.member.dto.MemberLoginResponseDTO;
import com.wedit.backend.api.member.dto.MemberSignupRequestDTO;
import com.wedit.backend.api.member.dto.SocialMemberAdditionalRequestDTO;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.PhoneNumberVerification;
import com.wedit.backend.api.member.entity.Role;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.member.repository.PhoneNumberVerificationRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final PhoneNumberVerificationRepository phoneNumberVerificationRepository;

	public void signupMember(MemberSignupRequestDTO dto) {

		// OAuth2 도입 시 회원가입 플로우 별 별도 분기 필요

		// 이메일 중복 검사
		if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_DUPLICATE_EMAIL.getMessage());
		}

		// 전화번호 중복 검사
		if (memberRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_DUPLICATE_PHONE.getMessage());
		}

		// 전화번호 인증 여부 검사
		PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(
				dto.getPhoneNumber())
			.orElseThrow(
				() -> new BadRequestException(ErrorStatus.BAD_REQUEST_MISSING_PHONE_NUMBER_VERIFICATION.getMessage()));
		if (!phoneNumberVerification.isVerified()) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_MISSING_PHONE_NUMBER_VERIFICATION.getMessage());
		}

		// Member 엔티티 생성 후 DB 저장 (폼 회원가입)
		Member member = Member.builder()
			.email(dto.getEmail())
			.password(passwordEncoder.encode(dto.getPassword()))
			.oauthId(null)
			.name(dto.getName())
			.phoneNumber(dto.getPhoneNumber())
			.birthDate(dto.getBirthDate())
			.weddingDate(dto.getWeddingDate())
			.role(Role.ROLE_USER)
			.type(dto.getType())
			.build();

		memberRepository.save(member);
	}

	public MemberLoginResponseDTO loginMember(MemberLoginRequestDTO dto) {

		// email 로 회원 조회
		Member member = memberRepository.findByEmail(dto.getEmail())
			.orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_FOUND_EMAIL.getMessage()));

		// 비밀번호 검증
		if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
			throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_EMAIL_OR_PASSWORD.getMessage());
		}

		// JWT 토큰 생성 (액세스, 리프레쉬)
		Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getId(), member.getEmail(),
			member.getRole());

		return new MemberLoginResponseDTO(
			member.getName(),
			member.getEmail(),
			tokens.get("accessToken"),
			tokens.get("refreshToken"),
			member.getRole().name()
		);
	}

	// Member 필드 수정 서비스 로직 (이메일, 비밀번호, 이름, 결혼예정일 등)
	public void socialLogin(String memberEmail, SocialMemberAdditionalRequestDTO socialMemberAdditionalRequestDTO) {

		// 전화번호 중복 검사
		if (memberRepository.findByPhoneNumber(socialMemberAdditionalRequestDTO.getPhoneNumber()).isPresent()) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_DUPLICATE_PHONE.getMessage());
		}

		// 전화번호 인증 여부 검사
		PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(
				socialMemberAdditionalRequestDTO.getPhoneNumber())
			.orElseThrow(
				() -> new BadRequestException(ErrorStatus.BAD_REQUEST_MISSING_PHONE_NUMBER_VERIFICATION.getMessage()));
		if (!phoneNumberVerification.isVerified()) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_MISSING_PHONE_NUMBER_VERIFICATION.getMessage());
		}

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Member build = member.toBuilder()
			.birthDate(socialMemberAdditionalRequestDTO.getBirthDate())
			.phoneNumber(socialMemberAdditionalRequestDTO.getPhoneNumber())
			.type(socialMemberAdditionalRequestDTO.getType())
			.weddingDate(socialMemberAdditionalRequestDTO.getWeddingDate())
			.build();
		memberRepository.save(build);
	}
}
