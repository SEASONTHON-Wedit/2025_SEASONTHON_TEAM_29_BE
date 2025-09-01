package com.wedit.backend.api.aws.s3.controller;

import com.wedit.backend.api.aws.s3.dto.ImagePutRequestDTO;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.aws.s3.util.ImageUtil;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final JwtService jwtService;

    @Operation(
            summary = "단일 S3 업로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 단일 파일을 업로드할 수 있도록 PreSigned URL을 반환합니다. <br>"
                    + "액세스 토큰을 통해 사용자를 식별하며, 파일 크기와 타입에 대한 검증을 수행합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "String domain : 도메인(review, vendor, etc.) <br>"
                    + "String filename : 파일 이름 <br>"
                    + "String contentType : image/jpeg, jpg, png, gif, webp 혹은 video/mp4, quicktime, x-matroska, webm <br>"
                    + "Long contentLength : 파일 크기 <br>"
                    + "Long entityId : 이미지 엔티티 ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PreSigned PUT URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/upload-url")
    public ResponseEntity<ApiResponse<String>> getPresignedPutUrl(
            @RequestBody ImagePutRequestDTO requestDTO,
            @RequestHeader("Authorization") String reqToken) {

        // 1. 액세스 토큰 추출 및 MemberId 추출, 없다면 예외
        Long memberId = extractMemberId(reqToken);

        // 2. 파일 타입 및 사이즈 검증
        validateFile(requestDTO.getContentType(), requestDTO.getContentLength());

        // 3. PreSigned URL 발급
        String url = s3Service.generatePresignedPutUrl(
                requestDTO.getDomain(),
                requestDTO.getFilename(),
                requestDTO.getContentType(),
                requestDTO.getContentLength(),
                memberId,
                requestDTO.getEntityId()
        );
        
        // 추후 도메인별 이미지 메타 저장 혹은 서비스에 위임

        // 4. 성공 응답과 PreSigned URL 반환
        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, url);
    }

    @Operation(
            summary = "복수 S3 업로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 복수 파일을 업로드할 수 있도록 PreSigned URL을 반환합니다. <br>"
                    + "액세스 토큰을 통해 사용자를 식별하며, 파일 크기와 타입에 대한 검증을 수행합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "String domain : 도메인(review, vendor, etc.) <br>"
                    + "String filename : 파일 이름 <br>"
                    + "String contentType : image/jpeg, jpg, png, gif, webp 혹은 video/mp4, quicktime, x-matroska, webm <br>"
                    + "Long contentLength : 파일 크기 <br>"
                    + "Long entityId : 이미지 엔티티 ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PreSigned PUT URLs 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/upload-urls")
    public ResponseEntity<ApiResponse<List<String>>> getPresignedPutMultipleUrls(
            @RequestBody List<ImagePutRequestDTO> requestDTOs,
            @RequestHeader("Authorization") String reqToken) {

        // 1. 액세스 토큰 추출 및 MemberId 추출, 없다면 예외
        Long memberId = extractMemberId(reqToken);

        // 2. 각 DTO 파일 타입 및 사이즈 검증
        requestDTOs.forEach(dto -> validateFile(dto.getContentType(), dto.getContentLength()));

        // 3. 각 파일마다 PreSigned URL 발급
        List<String> urls = requestDTOs.stream()
                .map(dto -> s3Service.generatePresignedPutUrl(
                        dto.getDomain(),
                        dto.getFilename(),
                        dto.getContentType(),
                        dto.getContentLength(),
                        memberId,
                        dto.getEntityId()
                )).toList();

        // 추후 도메인별 이미지 메타 저장 혹은 서비스에 위임

        // 4. 성공 응답과 PreSigned URLs 반환
        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, urls);
    }

    @Operation(
            summary = "단일 S3 다운로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 단일 파일을 다운로드할 수 있도록 PreSigned URL을 반환합니다. <br>"
                    + "액세스 토큰을 통해 사용자를 식별하며, 파일 크기와 타입에 대한 검증을 수행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PreSigned GET URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/download-url")
    public ResponseEntity<ApiResponse<Void>> getPresignedGetUrl(
            @RequestParam String domain,
            @RequestParam Long entityId,
            @RequestHeader("Authorization") String reqToken) {

        // 1. 액세스 토큰 추출 및 MemberId 추출, 없다면 예외
        Long memberId = extractMemberId(reqToken);

        // 추후 후기 기능 추가 시 String url 반환해야 함.

        return ApiResponse.successOnly(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS);
    }

    @Operation(
            summary = "복수 S3 다운로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 복수 파일을 다운로드할 수 있도록 PreSigned URL을 반환합니다. <br>"
                    + "액세스 토큰을 통해 사용자를 식별하며, 파일 크기와 타입에 대한 검증을 수행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PreSigned GET URLs 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/download-urls")
    public ResponseEntity<ApiResponse<Void>> getPresignedGetMultipleUrls(
            @RequestParam String domain,
            @RequestParam Long entityId,
            @RequestHeader("Authorization") String reqToken) {

        // 1. 액세스 토큰 추출 및 MemberId 추출, 없다면 예외
        Long memberId = extractMemberId(reqToken);

        // 추후 후기 기능 추가 시 List<String 혹은 DTO> urls 반환해야 함.

        return ApiResponse.successOnly(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS);
    }

    private Long extractMemberId(String reqToken) {
        String token =  reqToken.replace("Bearer ", "");
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    private void validateFile(String contentType, Long contentLength) {

        if (ImageUtil.isValidImageType(contentType)) {
            if (!ImageUtil.isValidImageSize(contentLength)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_IMAGE_SIZE.getMessage());
            }
        } else if (ImageUtil.isValidVideoType(contentType)) {
            if (!ImageUtil.isValidVideoSize(contentLength)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_VIDEO_SIZE.getMessage());
            }
        } else {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_NOT_SUPPORTED_MEDIA_TYPE.getMessage());
        }
    }
}