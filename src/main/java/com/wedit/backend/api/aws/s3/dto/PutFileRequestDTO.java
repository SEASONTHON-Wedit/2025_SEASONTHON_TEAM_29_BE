package com.wedit.backend.api.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PutFileRequestDTO {

    private String domain;
    private String filename;
    private String contentType;
    private Long contentLength;
}
