package com.wedit.backend.api.contract.controller;

import com.wedit.backend.api.contract.dto.request.ContractAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.response.ContractAvailabilityResponseDTO;
import com.wedit.backend.api.contract.service.ContractService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contract", description = "계약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contracts")
public class ContractController {
    
    private final ContractService contractService;
    
    @Operation(
        summary = "업체의 여러 달 계약 가능 시간 조회 API",
        description = "업체의 여러 달에 걸친 계약 가능한 시간을 페이징으로 조회합니다. " +
                     "예약된 시간과 이미 계약된 시간을 제외한 가용 시간을 확인할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "계약 가능 시간 조회 성공"
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
    @PostMapping("/{vendorId}/availability")
    public ResponseEntity<ApiResponse<Page<ContractAvailabilityResponseDTO>>> getVendorContractAvailabilities(
            @Parameter(description = "업체 ID", example = "1")
            @PathVariable Long vendorId,
            
            @Parameter(description = "계약 가능 시간 조회 요청 정보")
            @Valid @RequestBody ContractAvailabilityRequestDTO requestDTO,
            
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "페이지 크기", example = "3")
            @RequestParam(defaultValue = "3") int size,
            
            @Parameter(description = "정렬 방향 (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sort
    ) {
        // 정렬 방향 설정
        Sort.Direction direction = sort.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Pageable 객체 생성 (월 기준으로 정렬)
        Pageable pageable = PageRequest.of(page, size, direction, "month");
        
        // 서비스 호출
        Page<ContractAvailabilityResponseDTO> result = 
                contractService.getVendorContractAvailabilities(vendorId, requestDTO, pageable);
        
        return ApiResponse.success(SuccessStatus.CONTRACT_AVAILABILITY_GET_SUCCESS, result);
    }
}
