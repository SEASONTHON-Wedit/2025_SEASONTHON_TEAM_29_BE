package com.wedit.backend.api.aws.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "S3 Presigned URL 발급 요청 DTO")
public class PresignedUrlRequestDTO {

    @Schema(description = "관련 도메인의 ID (e.g., reviewId, vendorId 등)", example = "101")
    private Long domainId;

    @Schema(description = "업로드할 파일의 원본 이름 (확장자 포함)", example = "웨딩홀_전경사진.jpg")
    private String filename;

    @Schema(description = "파일의 MIME 타입 (e.g., 'image/jpeg', 'video/mp4')", example = "image/jpeg")
    private String contentType;

    @Schema(description = "파일 크기 (Bytes 단위). 이미지 15MB, 영상 100MB 제한", example = "2097152") // 2MB 예시
    private Long contentLength;
}
