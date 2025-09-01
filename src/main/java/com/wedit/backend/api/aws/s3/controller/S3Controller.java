package com.wedit.backend.api.aws.s3.controller;

import com.wedit.backend.api.aws.s3.dto.PutFileRequestDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final JwtService jwtService;

    @Operation(
            summary = "S3 업로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 파일을 업로드할 수 있도록 업로드용 PreSigned URL을 반환합니다. <br>"
                    + "요청 시 업로드할 파일의 도메인(domain), 원본 파일명(filename), Content-Type, Content-Length를 포함한 JSON 바디를 전달해야 합니다. <br>"
                    + "액세스 토큰을 통해 사용자를 식별하며, 파일 크기와 타입에 대한 검증을 수행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 URl 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/upload-url")
    public ResponseEntity<ApiResponse<String>> getPresignedPutUrl(
            @RequestBody PutFileRequestDTO requestDTO,
            @RequestHeader("Authorization") String reqToken) {

        // 1. 액세스 토큰 추출 및 MemberId 추출, 없다면 예외
        String token = reqToken.replace("Bearer ", "");
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));

        String contentType = requestDTO.getContentType();
        Long contentLength = requestDTO.getContentLength();

        // 2. 파일 Content-Type(image/video) 및 파일 크기 검증
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

        // 3. 도메인, 파일명, contentType, contentLength, memberId를 넘겨 S3 키 생성과 URL 발급
        String url = s3Service.generatePresignedPutUrl(
                requestDTO.getDomain(), requestDTO.getFilename(),
                requestDTO.getContentType(), requestDTO.getContentLength(),
                memberId
        );
        
        // 4. 성공 응답과 PreSigned URL 반환
        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, url);
    }


    @Operation(
            summary = "S3 다운로드용 PreSigned URL 발급 API",
            description = "클라이언트가 S3에 직접 파일을 다운로드할 수 있도록 다운로드용 PreSigned URL을 반환합니다. <br>"
                    + "요청 시 업로드할 파일의 S3 객체 키(key)와 Content-Type, Content-Length를 쿼리 파라미터로 전달해야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 URl 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/upload-url")
    public ResponseEntity<ApiResponse<String>> getPresignedGetUrl(@RequestParam("key") String key) {



        return ApiResponse.success(SuccessStatus.S3_PUT_URL_CREATE_SUCCESS, url);
    }
}
