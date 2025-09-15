package com.wedit.backend.api.aws.s3.controller;

import com.wedit.backend.api.aws.s3.dto.PresignedUrlRequestDTO;
import com.wedit.backend.api.aws.s3.dto.PresignedUrlResponseDTO;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.aws.s3.util.MediaUtil;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "S3", description = "S3 미디어(이미지/비디오/오디오) API 입니다.")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final JwtService jwtService;


    @Operation(summary = "단일 미디어 업로드용 Presigned URL 발급",
            description = """
                하나의 미디어 파일(이미지/비디오/오디오)을 S3에 업로드하기 위한 Presigned URL을 발급합니다.
                
                **지원 파일 타입:**
                - 이미지: jpg, jpeg, png, gif, webp (최대 15MB)
                - 비디오: mp4, mov, avi (최대 100MB)
                
                **도메인별 사용:**
                - REVIEW: 후기 이미지/동영상
                - VENDOR: 업체 로고/대표 이미지  
                - INVITATION: 초청장 갤러리 이미지
                """,
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 타입 또는 크기 초과", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @PutMapping("/{domain}/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> getSinglePutUrl(
            @Parameter(description = "업로드 도메인", example = "REVIEW") @PathVariable @NotNull MediaDomain domain,
            @Valid @RequestBody PresignedUrlRequestDTO reqDto,
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        validateFile(reqDto.getContentType(), reqDto.getContentLength());

        PresignedUrlResponseDTO dto = s3Service.generatePresignedPutUrl(reqDto, domain.name(), memberId);

        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, dto);
    }

    @Operation(summary = "복수 미디어 업로드용 Presigned URL 발급",
            description = """
                여러 개의 미디어 파일을 S3에 업로드하기 위한 Presigned URL 리스트를 발급합니다.
                
                **배치 업로드:**
                - 최대 10개 파일까지 동시 처리 가능
                - 각 파일은 개별 검증 수행
                - 하나라도 실패 시 전체 요청 실패
                
                **사용 예시:**
                - 후기 작성 시 여러 이미지 동시 업로드
                - 갤러리 사진 일괄 업로드
                """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 목록 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 타입 또는 크기 초과", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @PutMapping("/{domain}/upload-urls")
    public ResponseEntity<ApiResponse<List<PresignedUrlResponseDTO>>> getMultiplePutUrl(
            @Parameter(description = "업로드 도메인", example = "VENDOR") @PathVariable @NotNull MediaDomain domain,
            @Valid @RequestBody @NotEmpty(message = "파일 목록은 비어있을 수 없습니다") List<PresignedUrlRequestDTO> reqDtos,
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        for (var req : reqDtos) {
            validateFile(req.getContentType(), req.getContentLength());
        }

        List<PresignedUrlResponseDTO> dtos = reqDtos.stream()
                .map(req -> s3Service.generatePresignedPutUrl(req, domain.name(), memberId))
                .toList();

        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, dtos);
    }


    private Long extractMemberId(String reqToken) {
        String token =  reqToken.replace("Bearer ", "");
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }

    private void validateFile(String contentType, Long contentLength) {

        if (!MediaUtil.isValidContentType(contentType)) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_NOT_SUPPORTED_MEDIA_TYPE.getMessage());
        }
        if (!MediaUtil.isValidFileSize(contentType, contentLength)) {
            if (MediaUtil.isValidImageType(contentType)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_IMAGE_SIZE.getMessage());
            } else {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_VIDEO_SIZE.getMessage());
            }
        }
    }
}