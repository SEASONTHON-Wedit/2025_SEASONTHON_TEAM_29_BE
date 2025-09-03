package com.wedit.backend.api.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlRequestDTO {

    private Long domainId;
    private String filename;
    private String contentType;
    private Long contentLength;
}
