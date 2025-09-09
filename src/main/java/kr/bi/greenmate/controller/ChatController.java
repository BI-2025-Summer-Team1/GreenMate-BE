package kr.bi.greenmate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.bi.greenmate.dto.ChatHistoryResponse;
import kr.bi.greenmate.dto.ChatMessageRequest;
import kr.bi.greenmate.dto.ChatMessageResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.service.ChatRedisService;
import kr.bi.greenmate.service.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "챗봇", description = "챗봇 관련 API")
public class ChatController {

	private final ChatService chatService;
	private final ChatRedisService chatRedisService;

	@PostMapping("/message")
	@Operation(summary = "메시지 전송", description = "챗봇에게 메시지를 전송합니다.")
	public ResponseEntity<ChatMessageResponse> sendMessage(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody ChatMessageRequest request
	) {
		ChatMessageResponse response = chatService.sendMessage(user, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/current-session")
	@Operation(summary = "현재 세션 조회", description = "사용자의 현재 활성 세션 ID를 조회합니다.")
	public ResponseEntity<Long> getCurrentSessionId(@AuthenticationPrincipal User user) {
		Long sessionId = chatRedisService.getSessionId(user.getId());
		return ResponseEntity.ok(sessionId);
	}

	@GetMapping("/history/{sessionId}")
	@Operation(summary = "대화 히스토리 조회", description = "특정 세션의 대화 히스토리를 조회합니다.")
	public ResponseEntity<ChatHistoryResponse> getChatHistory(
		@AuthenticationPrincipal User user,
		@PathVariable Long sessionId,
		@RequestParam(required = false) Long cursor
	) {
		ChatHistoryResponse response = chatService.getChatHistory(user, sessionId, cursor);
		return ResponseEntity.ok(response);
	}

}
