package com.wedit.backend.api.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageGetResponseDTO {

    private Long entityId;
    private String url;
}
