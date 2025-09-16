package kr.bi.greenmate.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Gemini API 응답")
public class GeminiResponse {

	@Schema(description = "응답 후보들")
	private List<Candidate> candidates;

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "응답 후보")
	public static class Candidate {
		@Schema(description = "응답 내용")
		private Content content;

		@Schema(description = "완료 이유", example = "STOP")
		private String finishReason;

		@Schema(description = "평균 로그 확률")
		private Double avgLogprobs;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "응답 내용")
	public static class Content {
		@Schema(description = "역할", example = "model")
		private String role;

		@Schema(description = "하나의 메시지를 구성하는 부분들 (텍스트, 이미지 등)")
		private List<Part> parts;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "실제 텍스트")
	public static class Part {
		@Schema(description = "텍스트", example = "과자는 비닐, 플라스틱 등 입니다.")
		private String text;
	}
}
