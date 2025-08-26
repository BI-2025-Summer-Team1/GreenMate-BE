package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 생성 응답")
public class CommunityPostCreateResponse {
	@Schema(description = "생성된 게시글 ID", example = "1")
	private Long postId;
}
