package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 생성 요청")
public class CommunityPostCreateRequest {
	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 20, message = "제목은 20자 이하여야 합니다.")
	@Schema(description = "게시글 제목", example = "환경 보호에 대한 이야기", maxLength = 20)
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 500, message = "내용은 500자 이하여야 합니다.")
	@Schema(description = "게시글 내용", example = "오늘 환경 보호에 대한 생각을 나누고 싶습니다.", maxLength = 500)
	private String content;
}
