package kr.bi.greenmate.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgreementResponse {

	@Schema(description = "약관 ID")
	private Long id;

	@Schema(description = "약관 제목")
	private String title;

	@Schema(description = "약관 내용")
	private String content;

	@Schema(description = "필수 동의 여부")
	private boolean isRequired;

	@Schema(description = "약관 생성일")
	private LocalDateTime createdAt;
}
