package com.wedit.backend.api.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresignedUrlResponseDTO {

    private String presignedUrl;
}
