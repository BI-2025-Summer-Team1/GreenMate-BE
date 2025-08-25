package kr.bi.greenmate.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentPostCreationRequest {

	@Schema(description = "모집글 제목", example = "함께 쓰레기 주우러 가실 분 구해요!", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 100, message = "제목은 100자 이내여야 합니다.")
	private String title;

	@Schema(description = "모집글 내용", example = "이번 주말, 한강에서 플로깅 함께 하실 분들을 모집합니다. 자세한 내용은 본문을 참고해주세요.", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 2000, message = "내용은 2000자 이내여야 합니다.")
	private String content;

	@Schema(description = "활동 일자", example = "2025-10-27T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "활동일자는 필수입니다.")
	private LocalDateTime activityDate;

	@Schema(description = "모집 종료일", example = "2025-10-20T18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "모집 종료일은 필수입니다.")
	private LocalDateTime recruitmentEndDate;

}
