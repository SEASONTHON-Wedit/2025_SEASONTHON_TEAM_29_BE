package com.wedit.backend.api.aws.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "S3 Presigned URL 발급 응답 DTO")
public class PresignedUrlResponseDTO {

    @Schema(description = "S3에 저장된/저장될 객체의 고유 키(경로). 파일 조회 및 삭제 시 사용됩니다.",
            example = "review/1/images/101/a1b2c3d4-e5f6-...._웨딩홀_전경사진.jpg")
    private String s3Key;

    @Schema(description = "실제 파일 업로드/다운로드에 사용될, 일정 시간 동안만 유효한 임시 URL",
            example = "https://[your-bucket-name].s3.[region].amazonaws.com/[object-key]?X-Amz-Algorithm=...")
    private String presignedUrl;
}
