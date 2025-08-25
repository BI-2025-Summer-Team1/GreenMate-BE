package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "좋아요 응답")
public class CommunityPostLikeResponse {
	@Schema(description = "좋아요 여부", example = "true")
	private Boolean isLiked;

	@Schema(description = "총 좋아요 수", example = "42")
	private Long likeCount;
}
