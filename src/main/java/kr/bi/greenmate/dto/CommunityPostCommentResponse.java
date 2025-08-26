package kr.bi.greenmate.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "커뮤니티 댓글 응답 DTO")
public class CommunityPostCommentResponse {

	@Schema(description = "댓글 ID")
	private Long id;

	@Schema(description = "댓글 작성자 ID")
	private Long userId;

	@Schema(description = "댓글 작성자 닉네임")
	private String nickname;

	@Schema(description = "댓글 내용")
	private String content;

	@Schema(description = "댓글 이미지 URL / nullable")
	private String imageUrl;

	@Schema(description = "작성 시간")
	private LocalDateTime createdAt;
}
