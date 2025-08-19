package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPostLikeResponse {

    @Schema(description = "좋아요 여부", example = "true")
    private boolean liked; 

    @Schema(description = "좋아요 개수", example = "1")
    private long likeCount; 
}
