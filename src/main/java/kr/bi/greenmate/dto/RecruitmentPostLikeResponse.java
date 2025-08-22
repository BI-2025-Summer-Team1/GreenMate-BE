package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "모집 게시물 좋아요 토글 및 좋아요 개수 응답 DTO")
public class RecruitmentPostLikeResponse {

    @Schema(description = "좋아요 여부", example = "true")
    private boolean liked; 

    @Schema(description = "좋아요 개수", example = "1")
    private long likeCount; 
}
