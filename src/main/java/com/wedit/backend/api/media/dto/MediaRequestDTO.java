package com.wedit.backend.api.media.dto;

import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.entity.enums.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MediaRequestDTO {

    @Schema(description = "S3 Presigned URL 발급 시 반환된 S3 객체 키")
    private String mediaKey;

    @Schema(description = "파일의 원본 Content-Type")
    private String contentType;

    @Schema(description = "목록 내 미디어의 정렬 순서", defaultValue = "0")
    private int sortOrder;

    /**
     * DTO를 Media 엔티티로 변환합니다.
     */
    public Media toEntity(MediaDomain ownerDomain, Long ownerId) {
        return Media.builder()
                .ownerDomain(ownerDomain)
                .ownerId(ownerId)
                .mediaKey(this.mediaKey)
                .contentType(this.contentType)
                .mediaType(determineMediaType(this.contentType))
                .sortOrder(this.sortOrder)
                .build();
    }

    public Media toEntity(MediaDomain ownerDomain, Long ownerId, String groupTitle) {
        return Media.builder()
                .ownerDomain(ownerDomain)
                .ownerId(ownerId)
                .mediaKey(this.mediaKey)
                .contentType(this.contentType)
                .mediaType(determineMediaType(this.contentType))
                .sortOrder(this.sortOrder)
            	.groupTitle(groupTitle)
                .build();
    }

    private MediaType determineMediaType(String contentType) {
        if (contentType == null) return null;
        String lower = contentType.toLowerCase();
        if (lower.startsWith("image/")) return MediaType.IMAGE;
        if (lower.startsWith("video/")) return MediaType.VIDEO;
        if (lower.startsWith("audio/")) return MediaType.AUDIO;
        return null;
    }
}
