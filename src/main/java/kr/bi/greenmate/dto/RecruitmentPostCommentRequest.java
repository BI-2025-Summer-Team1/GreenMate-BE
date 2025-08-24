package kr.bi.greenmate.dto;

import org.springframework.lang.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RecruitmentPostCommentRequest {

	@NotBlank(message = "댓글 내용은 필수입니다.")
	@Schema(description = "댓글 내용", example = "정말 좋은 활동이네요!")
	@Size(max = 300, message = "댓글 내용은 300자 이하여야 합니다.")
	private String content;

	@Nullable
	@Schema(description = "부모 댓글 ID (대댓글인 경우에만 사용)", example = "10")
	private Long parentCommentId;
}
