package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "챗봇 메시지 전송 요청")
public class ChatMessageRequest {

	@NotBlank(message = "메시지는 필수입니다")
	@Size(max = 300, message = "메시지는 300자를 초과할 수 없습니다")
	@Schema(description = "사용자 메시지", example = "프링글스 통은 어떻게 분리수거하나요?", maxLength = 300)
	private String message;
}