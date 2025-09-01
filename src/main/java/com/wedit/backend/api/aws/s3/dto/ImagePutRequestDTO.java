package com.wedit.backend.api.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImagePutRequestDTO {

    private String domain;
    private String filename;
    private String contentType;
    private Long contentLength;
    private Long entityId;
}
