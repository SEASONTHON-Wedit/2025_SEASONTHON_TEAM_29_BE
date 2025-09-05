package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "페이징 정보 DTO")
public class PaginationInfoDTO {
    
    @Schema(description = "현재 페이지", example = "1")
    private Integer currentPage;
    
    @Schema(description = "페이지 크기", example = "5")
    private Integer pageSize;
    
    @Schema(description = "현재 페이지의 실제 항목 수", example = "5")
    private Integer currentPageItems;
    
    @Schema(description = "전체 항목 수", example = "150")
    private Long totalItems;
    
    @Schema(description = "전체 페이지 수", example = "30")
    private Integer totalPages;
    
    @Schema(description = "첫 페이지 여부", example = "true")
    private Boolean isFirst;
    
    @Schema(description = "마지막 페이지 여부", example = "false")
    private Boolean isLast;
    
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
}
