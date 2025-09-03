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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "S3", description = "S3 PreSigned URL 이미지 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final JwtService jwtService;


    @Operation(
            summary = "단일 미디어 업로드용 Presigned URL 발급 API",
            description = "단일 미디어 파일에 대한 업로드용 PreSigned URL을 발급합니다. <br>"
                    + "액세스 토큰을 헤더(Authorization)에 보내면 사용자 인증합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "String domain : 업로드가 쓰이는 도메인 (ex. review, vendor, etc.) <br>"
                    + "Long domainId : 엔티티 ID (PK) <br>"
                    + "String filename : 원본 파일의 이름 (확장자 포함) <br>"
                    + "String contentType : image/jpeg, jpg, png, gif, webp 혹은 video/mp4, quicktime, x-matroska, webm <br>"
                    + "Long contentLength : 파일 크기 (이미지 15MB, 동영상 100MB 제한)"
    )
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

    @Operation(
            summary = "복수 미디어 업로드용 Presigned URL 발급 API",
            description = "단일 미디어 파일에 대한 업로드용 PreSigned URL을 발급합니다. <br>"
                    + "액세스 토큰을 헤더(Authorization)에 보내면 사용자 인증합니다."
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "String domain : 업로드가 쓰이는 도메인 (ex. review, vendor, etc.) <br>"
                    + "Long domainId : 엔티티 ID (PK) <br>"
                    + "String filename : 원본 파일의 이름 (확장자 포함) <br>"
                    + "String contentType : image/jpeg, jpg, png, gif, webp 혹은 video/mp4, quicktime, x-matroska, webm <br>"
                    + "Long contentLength : 파일 크기 (이미지 15MB, 동영상 100MB 제한)"
    )
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

    @Operation(
            summary = "복수 미디어 다운로드용 Presigned URL 발급 API"
    )
    @GetMapping("/{domain}/download-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> getSingleDownloadUrl(
            @PathVariable String domain,
            @RequestParam String key,
            @RequestHeader("Authorization") String reqToken) {

        extractMemberId(reqToken);

        PresignedUrlResponseDTO dto = s3Service.generatePresignedGetUrl(key);

        return ApiResponse.success(SuccessStatus.S3_GET_URL_CREATE_SUCCESS, dto);
    }

    @Operation(
            summary = "복수 미디어 다운로드용 Presigned URL 발급 API"
    )
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