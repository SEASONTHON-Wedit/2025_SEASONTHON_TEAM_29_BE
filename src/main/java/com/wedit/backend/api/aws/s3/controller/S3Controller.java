package com.wedit.backend.api.aws.s3.controller;

import com.wedit.backend.api.aws.s3.dto.PresignedUrlRequestDTO;
import com.wedit.backend.api.aws.s3.dto.PresignedUrlResponseDTO;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.aws.s3.util.ImageUtil;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "S3", description = "S3 미디어(이미지/비디오) API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final JwtService jwtService;


    @Operation(summary = "단일 미디어 업로드용 Presigned URL 발급",
            description = "하나의 미디어 파일(이미지/비디오)을 S3에 업로드하기 위한 Presigned URL을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 타입 또는 크기 초과", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @Parameters({
            @Parameter(name = "domain", description = "업로드 도메인(e.g., 'review', 'vendor')", required = true, example = "review"),
            @Parameter(name = "reqToken", hidden = true)
    })
    @PutMapping("/{domain}/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> getSinglePutUrl(
            @PathVariable String domain,
            @RequestBody PresignedUrlRequestDTO reqDto,
            @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        validateFile(reqDto.getContentType(), reqDto.getContentLength());

        PresignedUrlResponseDTO dto = s3Service.generatePresignedPutUrl(reqDto, domain, memberId);

        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, dto);
    }

    @Operation(summary = "복수 미디어 업로드용 Presigned URL 발급",
            description = "여러 개의 미디어 파일을 S3에 업로드하기 위한 Presigned URL 리스트를 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 목록 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 타입 또는 크기 초과", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @Parameters({
            @Parameter(name = "domain", description = "업로드 도메인(e.g., 'review', 'vendor')", required = true, example = "vendor"),
            @Parameter(name = "reqToken", hidden = true)
    })
    @PutMapping("/{domain}/upload-urls")
    public ResponseEntity<ApiResponse<List<PresignedUrlResponseDTO>>> getMultiplePutUrl(
            @PathVariable String domain,
            @RequestBody List<PresignedUrlRequestDTO> reqDtos,
            @RequestHeader("Authorization") String reqToken) {
        Long memberId = extractMemberId(reqToken);

        for (var req : reqDtos) {
            validateFile(req.getContentType(), req.getContentLength());
        }

        List<PresignedUrlResponseDTO> dtos = reqDtos.stream()
                .map(req -> s3Service.generatePresignedPutUrl(req, domain, memberId))
                .toList();

        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, dtos);
    }

    @Operation(summary = "단일 미디어 다운로드용 Presigned URL 발급",
            description = "S3에 저장된 단일 미디어 파일을 다운로드하기 위한 Presigned URL을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @Parameters({
            @Parameter(name = "domain", description = "다운로드 도메인", required = true, example = "review"),
            @Parameter(name = "reqToken", hidden = true)
    })
    @GetMapping("/{domain}/download-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> getSingleDownloadUrl(
            @PathVariable String domain,
            @Parameter(
                    description = "다운로드할 파일의 S3 객체 키 (파일 경로)",
                    required = true,
                    example = "review/1/images/10/a1b2c3d4_20250903_image.jpg"
            )
            @RequestParam String key,
            @RequestHeader("Authorization") String reqToken) {

        extractMemberId(reqToken);

        PresignedUrlResponseDTO dto = s3Service.generatePresignedGetUrl(key);

        return ApiResponse.success(SuccessStatus.S3_GET_URL_CREATE_SUCCESS, dto);
    }

    @Operation(summary = "복수 미디어 다운로드용 Presigned URL 발급",
            description = "S3에 저장된 여러 미디어 파일을 다운로드하기 위한 Presigned URL 리스트를 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "URL 목록 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
    })
    @Parameters({
            @Parameter(name = "domain", description = "다운로드 도메인", required = true, example = "vendor"),
            @Parameter(name = "reqToken", hidden = true)
    })
    @PostMapping("/{domain}/download-urls")
    public ResponseEntity<ApiResponse<List<PresignedUrlResponseDTO>>> getMultipleDownloadUrl(
            @PathVariable String domain,
            @RequestBody List<String> keys,
            @RequestHeader("Authorization") String reqToken) {

        extractMemberId(reqToken);

        List<PresignedUrlResponseDTO> dtos = keys.stream()
                .map(s3Service::generatePresignedGetUrl)
                .toList();

        return ApiResponse.success(SuccessStatus.S3_GET_URL_CREATE_SUCCESS, dtos);
    }


    private Long extractMemberId(String reqToken) {
        String token =  reqToken.replace("Bearer ", "");
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }

    private void validateFile(String contentType, Long contentLength) {

        if (!ImageUtil.isValidContentType(contentType)) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_NOT_SUPPORTED_MEDIA_TYPE.getMessage());
        }
        if (!ImageUtil.isValidFileSize(contentType, contentLength)) {
            if (ImageUtil.isValidImageType(contentType)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_IMAGE_SIZE.getMessage());
            } else {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_VIDEO_SIZE.getMessage());
            }
        }
    }
}