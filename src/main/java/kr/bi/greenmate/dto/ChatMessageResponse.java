package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.bi.greenmate.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 메시지 응답")
public class ChatMessageResponse {

	@Schema(description = "메시지 내용", example = "프링글스 통은 복합 재질로 되어 있어 분리수거가 중요합니다.")
	private String content;

	@Schema(description = "메시지 타입", example = "USER", allowableValues = {"USER", "ASSISTANT"})
	private ChatMessage.MessageType type;

	public static ChatMessageResponse from(ChatMessage message) {
		return ChatMessageResponse.builder()
			.content(message.getContent())
			.type(message.getType())
			.build();
	}
}
