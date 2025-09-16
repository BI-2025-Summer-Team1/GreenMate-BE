package kr.bi.greenmate.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 대화 히스토리 응답")
public class ChatHistoryResponse {

	@Schema(description = "메시지 목록")
	private List<ChatMessageResponse> messages;

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	private Boolean hasMore;

	@Schema(description = "다음 페이지 커서", example = "10")
	private String nextCursor;
}
