package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 세션 정보 응답")
public class ChatSessionResponse {

	@Schema(description = "세션 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private String sessionId;

	@Schema(description = "세션 생성일시", example = "2024-01-01T10:00:00")
	private LocalDateTime createdAt;

	@Schema(description = "마지막 메시지 시간", example = "2024-01-01T10:30:00")
	private LocalDateTime lastMessageAt;
}
