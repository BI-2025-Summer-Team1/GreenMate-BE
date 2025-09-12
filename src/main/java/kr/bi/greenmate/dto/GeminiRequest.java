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
@Schema(description = "Gemini API 요청")
public class GeminiRequest {

	@Schema(description = "대화의 전체 맥락")
	private List<Content> contents;

	@Schema(description = "생성 설정")
	private GenerationConfig generationConfig;

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "각각의 대화 (사용자 메시지, 답변)")
	public static class Content {
		@Schema(description = "하나의 메시지를 구성하는 부분들 (텍스트, 이미지 등)")
		private List<Part> parts;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "실제 텍스트")
	public static class Part {
		@Schema(description = "텍스트", example = "과자 분리수거 방법 알려줘.")
		private String text;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "생성 설정")
	public static class GenerationConfig {
		@Schema(description = "최대 출력 토큰 수", example = "1000")
		private Integer maxOutputTokens;

		@Schema(description = "창의성 (temperature)", example = "0.7")
		private Double temperature;
	}
}
