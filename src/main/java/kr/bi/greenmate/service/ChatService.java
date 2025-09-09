package kr.bi.greenmate.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.dto.ChatHistoryResponse;
import kr.bi.greenmate.dto.ChatMessageRequest;
import kr.bi.greenmate.dto.ChatMessageResponse;
import kr.bi.greenmate.entity.ChatMessage;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRedisService chatRedisService;
	private final GeminiApiService geminiApiService;

	private static final AtomicLong sessionIdGenerator = new AtomicLong(1);

	@Transactional
	public ChatMessageResponse sendMessage(User user, ChatMessageRequest request) {
		Long sessionId = getOrCreateSessionId(user.getId());

		ChatMessage userMessage = ChatMessage.builder()
			.sessionId(sessionId)
			.userId(user.getId())
			.content(request.getMessage())
			.type(ChatMessage.MessageType.USER)
			.build();

		ChatMessage savedUserMessage = chatMessageRepository.save(userMessage);
		chatRedisService.addMessageToHistory(user.getId(), sessionId, savedUserMessage);

		try {
			List<ChatMessage> history = chatRedisService.getChatHistory(user.getId(), sessionId);
			String response = geminiApiService.generateResponse(history);

			ChatMessage assistantMessage = ChatMessage.builder()
				.sessionId(sessionId)
				.userId(user.getId())
				.content(response)
				.type(ChatMessage.MessageType.ASSISTANT)
				.build();

			ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);
			chatRedisService.addMessageToHistory(user.getId(), sessionId, savedAssistantMessage);

			return ChatMessageResponse.from(savedAssistantMessage);

		} catch (Exception e) {
			log.error("Gemini API 호출 실패: userId={}, sessionId={}", user.getId(), sessionId, e);

			ChatMessage errorMessage = ChatMessage.builder()
				.sessionId(sessionId)
				.userId(user.getId())
				.content("죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
				.type(ChatMessage.MessageType.ASSISTANT)
				.build();

			ChatMessage savedErrorMessage = chatMessageRepository.save(errorMessage);
			return ChatMessageResponse.from(savedErrorMessage);
		}
	}


	public ChatHistoryResponse getChatHistory(User user, Long sessionId, Long cursor) {
		List<ChatMessage> messages;
		boolean hasMore = false;

		if (cursor == null) {
			messages = chatMessageRepository.findByUserIdAndSessionIdOrderByCreatedAtDesc(
				user.getId(), sessionId, PageRequest.of(0, 10)
			);
		} else {
			messages = chatMessageRepository.findByUserIdAndSessionIdAndIdLessThanOrderByCreatedAtDesc(
				user.getId(), sessionId, cursor, PageRequest.of(0, 10)
			);
		}

		if (messages.size() == 10) {
			hasMore = true;
		}

		String nextCursor = hasMore ? messages.get(messages.size() - 1).getId().toString() : null;

		List<ChatMessageResponse> responseMessages = messages.stream()
			.map(ChatMessageResponse::from)
			.toList();

		return ChatHistoryResponse.builder()
			.messages(responseMessages)
			.hasMore(hasMore)
			.nextCursor(nextCursor)
			.build();
	}

	private Long getOrCreateSessionId(Long userId) {
		Long sessionId = chatRedisService.getCurrentSessionId(userId);

		if (sessionId == null) {
			sessionId = sessionIdGenerator.getAndIncrement();
			chatRedisService.setCurrentSessionId(userId, sessionId);
		}

		return sessionId;
	}
}
