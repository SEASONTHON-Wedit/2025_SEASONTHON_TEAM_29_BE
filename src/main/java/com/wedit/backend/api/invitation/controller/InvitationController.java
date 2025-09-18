package com.wedit.backend.api.invitation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/invitation")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Invitation", description = "Invitation 관련 API 입니다.")
public class InvitationController {
	private final InvitationService invitationService;

	@Operation(
		summary = "청첩장 생성 API", 
		description = """
			청첩장을 생성합니다. 사용자 인증이 필요합니다.
			
			**요청 정보:**
			- 기본 정보: 신랑/신부 이름, 결혼식 장소 및 날짜
			- 인사말, 갤러리 이미지 등 포함
			- 현재는 ending, account, background는 없어서 빼고 보내주시면 됩니다!
			
			**요청 예시:**
			```json
			{
			  "groomName": "김철수",
			  "brideName": "이영희", 
			  "marriagePlace": "아펜가모 선릉점",
			  "marriageDate": "2025-10-15",
			  "greetings": "저희 두 사람이 사랑의 결실을 맺게 되었습니다..."
			}
			```
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "청첩장 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 청첩장이 존재하거나 잘못된 요청", content = @Content),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
	})
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createInvitation(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody InvitationCreateRequestDTO createRequestDTO) {
		
		invitationService.createInvitation(userDetails.getUsername(), createRequestDTO);

		return ApiResponse.successOnly(SuccessStatus.INVITATION_CREATE_SUCCESS);
	}

	@Operation(
		summary = "청첩장 조회 API", 
		description = """
			현재 사용자의 청첩장 정보를 조회합니다. 사용자 인증이 필요합니다.
			
			**응답 정보:**
			- 신랑/신부 기본 정보
			- 결혼식 장소 및 날짜/시간
			- **marriagePlace.location**: vendorName을 통해 자동으로 조회된 업체의 전체 주소
			- 인사말 및 갤러리 이미지
			- 템플릿 및 테마 정보
			
			**예시 응답:**
			```json
			{
			  "code": 200,
			  "message": "청첩장 조회 성공",
			  "data": {
			    "marriagePlace": {
			      "vendorName": "아펠가모 선릉",
			      "floorAndHall": "2층 로얄홀",
			      "drawSketchMap": true,
			      "location": "서울 강남구 테헤란로 322 한신인터벨리24 빌딩 4층"
			    },
			    "basicInformation": {
			      "groomName": "김철수",
			      "brideName": "이영희"
			    },
			    "marriageDate": {
			      "date": "2025-10-15",
			      "time": "14:00"
			    },
			    "greetings": "저희 두 사람이 사랑의 결실을 맺게 되었습니다...",
			    "galleryImages": ["https://cdn.example.com/image1.jpg"]
			  }
			}
			```
			
			**⭐ 새로운 기능:**
			- `marriagePlace.location` 필드가 자동으로 설정됩니다
			- `vendorName`에 해당하는 업체를 데이터베이스에서 조회하여 `fullAddress + addressDetail`을 location으로 제공
			- 업체를 찾을 수 없는 경우 location은 null로 반환됩니다
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "청첩장 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "청첩장을 찾을 수 없습니다", content = @Content)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<InvitationGetResponseDTO>> getInvitation(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		InvitationGetResponseDTO invitation = invitationService.getInvitation(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.INVITATION_GET_SUCCESS, invitation);
	}
}
