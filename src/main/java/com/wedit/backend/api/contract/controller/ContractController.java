package com.wedit.backend.api.contract.controller;

import com.wedit.backend.api.contract.dto.request.ContractAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.request.AvailableTimeRequestDTO;
import com.wedit.backend.api.contract.dto.request.SimpleAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.request.ContractCreateRequestDTO;
import com.wedit.backend.api.contract.dto.request.SimpleContractRequestDTO;
import com.wedit.backend.api.contract.dto.response.ContractAvailabilityResponseDTO;
import com.wedit.backend.api.contract.dto.response.AvailableTimeResponseDTO;
import com.wedit.backend.api.contract.dto.response.SimpleAvailabilityResponseDTO;
import com.wedit.backend.api.contract.dto.response.TimeSlotAvailabilityListResponseDTO;
import com.wedit.backend.api.contract.dto.response.ContractCreateResponseDTO;
import com.wedit.backend.api.contract.dto.response.SimpleContractResponseDTO;
import com.wedit.backend.api.contract.service.ContractService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Reservation/Estimate/Contract", description = "Reservation, Estimate, Contract 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/contracts")
public class ContractController {
    
    private final ContractService contractService;
    
    @Operation(
        summary = "업체의 시간대별 가용성 조회 API",
        description = "업체의 여러 달에 걸친 각 시간대별 가용성을 개별 항목으로 조회합니다. " +
                     "각 시간대마다 가격 정보와 예상 보증인원, 식대 정보를 포함합니다. " +
                     "예약 가능한 시간대만 조회하여 DB 부하를 최소화합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "시간대별 가용성 조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청입니다."
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "업체를 찾을 수 없습니다."
        )
    })
    @PostMapping("/{vendorId}/contract")
    public ResponseEntity<ApiResponse<TimeSlotAvailabilityListResponseDTO>> getTimeSlotAvailabilities(
            @Parameter(description = "업체 ID", example = "1")
            @PathVariable Long vendorId,
            
            @Parameter(description = "시간대별 가용성 조회 요청 정보")
            @Valid @RequestBody SimpleAvailabilityRequestDTO requestDTO
    ) {
        TimeSlotAvailabilityListResponseDTO result = contractService.getTimeSlotAvailabilities(vendorId, requestDTO);
        return ApiResponse.success(SuccessStatus.CONTRACT_AVAILABILITY_GET_SUCCESS, result);
    }
    
    @Operation(
        summary = "간단한 계약 생성 API",
        description = "날짜와 시간만으로 간단하게 계약을 생성합니다. " +
                     "예: 2025.09.04 10:00 이런 식으로 간단하게!"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "계약 생성 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "업체 또는 회원을 찾을 수 없습니다."
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "해당 시간대는 이미 예약되어 있습니다."
        )
    })
    @PostMapping("/{vendorId}/create")
    public ResponseEntity<ApiResponse<SimpleContractResponseDTO>> createSimpleContract(
            @Parameter(description = "업체 ID", example = "1")
            @PathVariable Long vendorId,
            
            @Parameter(description = "회원 ID", example = "1")
            @RequestParam Long memberId,
            
            @Parameter(description = "간단한 계약 생성 요청 정보")
            @Valid @RequestBody SimpleContractRequestDTO requestDTO
    ) {
        SimpleContractResponseDTO result = contractService.createSimpleContract(vendorId, memberId, requestDTO);
        return ResponseEntity.status(201).body(
                ApiResponse.<SimpleContractResponseDTO>builder()
                        .status(201)
                        .success(true)
                        .message("계약이 성공적으로 생성되었습니다.")
                        .data(result)
                        .build()
        );
    }
}
