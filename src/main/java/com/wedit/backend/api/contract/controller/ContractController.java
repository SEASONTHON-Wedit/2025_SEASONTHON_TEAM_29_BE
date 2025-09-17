package com.wedit.backend.api.contract.controller;

import com.wedit.backend.api.contract.dto.*;
import com.wedit.backend.api.contract.service.ContractService;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contract", description = "Contract 계약 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/contracts")
public class ContractController {

    private final ContractService contractService;
    private final JwtService jwtService;


    @Operation(
            summary = "계약 가능 시간 슬롯 조회",
            description = "특정 상품에 대해 여러 달에 걸친 예약 가능 시간 슬롯 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "슬롯 조회 성공")
    })
    @Parameters({
            @Parameter(name = "productId", description = "상품 ID", required = true, example = "1"),
            @Parameter(name = "months", description = "조회 희망 월(month) 리스트", required = true, example = "8")
    })
    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponseDTO>>> getAvailableSlots(
            @Valid @ModelAttribute AvailableSlotsRequestDTO request) {

        List<AvailableSlotResponseDTO> response = contractService.getAvailableContractSlots(request);

        return ApiResponse.success(SuccessStatus.CONTRACT_AVAILABILITY_GET_SUCCESS, response);
    }

    @Operation(
            summary = "계약 생성",
            description = "사용자가 선택한 시간 슬롯으로 계약을 생성합니다. 액세스 토큰 필요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "계약 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 예약된 슬롯", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 슬롯", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ContractCreateResponseDTO>> createContract(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody ContractCreateRequestDTO request) {

        Long memberId = extractMemberId(reqToken);

        ContractCreateResponseDTO response = contractService.createContract(memberId, request);

        return ApiResponse.success(SuccessStatus.CONTRACT_CREATE_SUCCESS, response);
    }

    @Operation(summary = "나의 계약 목록 조회 (마이페이지 내 계약건 탭)",
            description = "다가오는 계약, 지난 계약을 모두 포함하여 이행일 순으로 페이징 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "페이지 당 항목 수", in = ParameterIn.QUERY, example = "5")
    })
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyContractsResponseDTO>> getMyContracts(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 5) Pageable pageable) {

        Long memberId = extractMemberId(reqToken);

        MyContractsResponseDTO response = contractService.getMyContracts(memberId, pageable);

        return ApiResponse.success(SuccessStatus.MY_CONTRACT_GET_SUCCESS, response);
    }

    @Operation(
            summary = "계약 상세 조회",
            description = "특정 계약의 상세 정보를 조회합니다. 액세스 토큰 필요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 계약", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<ContractDetailDTO>> getContractDetail(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "계약 ID", example = "1") @PathVariable @Positive Long contractId) {

        Long memberId = extractMemberId(reqToken);

        ContractDetailDTO response = contractService.getContractDetail(memberId, contractId);

        return ApiResponse.success(SuccessStatus.CONTRACT_DETAIL_GET_SUCCESS, response);
    }

    @Operation(
            summary = "후기 작성 가능 계약 목록 조회 (후기 작성하러 가기 페이지)",
            description = "이행일이 지난 계약 목록을 페이징 조회합니다."
    )
    @GetMapping("/my/reviewable")
    public ResponseEntity<ApiResponse<Page<ReviewableContractDTO>>> getReviewableContracts(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 5) Pageable pageable) {

        Long memberId = extractMemberId(reqToken);

        Page<ReviewableContractDTO> response = contractService.getReviewableContracts(memberId, pageable);

        return ApiResponse.success(SuccessStatus.REVIEWABLE_CONTRACT_GET_SUCCESS, response);
    }

    @Operation(
            summary = "계약 가능 시간 슬롯 일괄 생성",
            description = "특정 상품의 계약 가능 시간(AvailableSlot)들을 한 번에 등록합니다."
    )
    @PostMapping("/available-slots")
    public ResponseEntity<ApiResponse<Void>> createAvailableSlots(
            @Valid @RequestBody AvailableSlotCreateRequestDTO request) {

        contractService.createSlots(request);

        return ApiResponse.successOnly(SuccessStatus.AVAILABLE_TIME_SLOT_CREATE_SUCCESS);
    }

    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
