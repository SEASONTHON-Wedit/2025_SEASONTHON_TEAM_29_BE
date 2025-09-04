package com.wedit.backend.api.vendor.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wedit.backend.api.vendor.entity.Address;
import com.wedit.backend.api.vendor.entity.dto.details.VendorDetailsDTO;
import com.wedit.backend.api.vendor.entity.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "업체 상세 정보 응답 DTO")
public class VendorDetailsResponseDTO {

    // --- 공통 업체 정보 ---
    @Schema(description = "업체 고유 ID", example = "42")
    private Long vendorId;

    @Schema(description = "업체 이름", example = "아펠가모 선릉")
    private String name;

    @Schema(description = "업체 카테고리")
    private Category category;

    @Schema(description = "업체 소개글", example = "품격 있는 채플 웨딩 스타일을 선도합니다...")
    private String description;

    @Schema(description = "업체 주소 정보")
    private Address address;

    // --- 카테고리별 상세 정보 ---
    @Schema(description = "카테고리별 상세 정보. 업체 category 값에 따라 구조가 달라집니다.")
    private VendorDetailsDTO details;

    // --- 이미지 정보 ---
    @Schema(description = "대표 이미지의 S3 Presigned URL", example = "https://s3...")
    private String mainImageUrl;    // 대표 이미지 URL
    @Schema(description = "그룹화된 이미지 목록. 상세 페이지의 섹션을 구성합니다.")
    private List<ImageGroupResponseDTO> imageGroups;    // 그룹화된 이미지들

    // 그룹화된 이미지 섹션 담는 내부 DTO
    @Getter
    @Builder
    @Schema(description = "이미지 그룹 정보를 담는 DTO")
    public static class ImageGroupResponseDTO {
        @Schema(description = "그룹 제목", example = "Wedding Hall")
        private String groupTitle;

        @Schema(description = "그룹 표시 순서", example = "0")
        private Integer groupSortOrder;

        @Schema(description = "그룹에 속한 이미지 목록")
        private List<ImageResponseDTO> images;
    }

    // 개별 이미지 내부 DTO
    @Getter
    @Builder
    @Schema(description = "개별 이미지 정보를 담는 DTO")
    public static class ImageResponseDTO {
        @Schema(description = "개별 이미지의 S3 Presigned URL", example = "https://s3...")
        private String imageUrl;

        @Schema(description = "그룹 내 이미지 표시 순서", example = "1")
        private Integer sortOrder;
    }
}
