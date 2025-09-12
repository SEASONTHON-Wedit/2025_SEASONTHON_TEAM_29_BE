package com.wedit.backend.api.contract.controller;

import com.wedit.backend.api.contract.dto.*;
import com.wedit.backend.api.contract.service.ContractService;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contracts")
public class ContractController {

    private final ContractService contractService;
    private final JwtService jwtService;


    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponseDTO>>> getAvailableSlots(
            @Valid @ModelAttribute AvailableSlotsRequestDTO request) {

        List<AvailableSlotResponseDTO> response = contractService.getAvailableContractSlots(request);

        return ApiResponse.success(SuccessStatus.CONTRACT_AVAILABILITY_GET_SUCCESS, response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContractCreateResponseDTO>> createContract(
            @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody ContractCreateRequestDTO request) {

        Long memberId = extractMemberId(reqToken);

        ContractCreateResponseDTO response = contractService.createContract(memberId, request);

        return ApiResponse.success(SuccessStatus.CONTRACT_CREATE_SUCCESS, response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyContractsResponseDTO>> getMyContracts(
            @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 3) Pageable pageable) {

        Long memberId = extractMemberId(reqToken);

        MyContractsResponseDTO response = contractService.getMyContracts(memberId, pageable);

        return ApiResponse.success(SuccessStatus.MY_CONTRACT_GET_SUCCESS, response);
    }

    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
