package kr.bi.greenmate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import kr.bi.greenmate.dto.GeminiRequest;
import kr.bi.greenmate.dto.GeminiResponse;
import kr.bi.greenmate.entity.ChatMessage;
import kr.bi.greenmate.exception.error.GeminiApiFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final @Qualifier("geminiRestClient") RestClient geminiRestClient;

	private static final int MAX_CONTEXT_MESSAGES = 8;

	@Retryable(
		retryFor = {
			IOException.class,
			TimeoutException.class
		},
		exclude = {RestClientResponseException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0, random = true)
	)
	public String generateResponse(List<ChatMessage> history) {
		try {
			String systemPrompt = buildSystemPrompt();
			String conversation = buildConversation(history);
			String finalPrompt = systemPrompt + "\n" + conversation;

			GeminiRequest.Part part = new GeminiRequest.Part(finalPrompt);
			GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
			GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig(1000, 0.7);
			GeminiRequest req = new GeminiRequest(List.of(content), config);

			GeminiResponse res = geminiRestClient.post()
				.uri(uriBuilder -> uriBuilder
					.path("/models/gemini-2.0-flash:generateContent")
					.queryParam("key", apiKey)
					.build())
				.body(req)
				.retrieve()
				.body(GeminiResponse.class);

			String answer = extractPrimaryText(res);
			String sanitized = sanitizeAssistantText(answer);
			log.debug("Gemini 응답 텍스트 길이: {}", sanitized != null ? sanitized.length() : 0);
			return sanitized;
		} catch (RestClientResponseException e) {
			log.error("Gemini API 응답 에러: {}", e.getMessage());
			throw new GeminiApiFailException(e.getMessage());
		} catch (Exception e) {
			log.error("Gemini API 호출 중 예외", e);
			throw new GeminiApiFailException(e.getMessage());
		}
	}

	private String sanitizeAssistantText(String text) {
		if (text == null) {
			return "";
		}
		String sanitized = text
			// literal escape sequences first
			.replace("\\r\\n", " ")
			.replace("\\n", " ")
			.replace("\\r", " ")
			// actual control characters
			.replace("\r", " ")
			.replace("\n", " ");

		sanitized = sanitized.replaceAll("\\s+", " ").trim();
		sanitized = sanitized.replaceFirst("^(?i)(어시스턴트|assistant|모델|model)\\s*:\\s*", "");
		return sanitized;
	}

	private String buildSystemPrompt() {
		return String.join("\n",
			"당신은 사용자가 환경 보호를 쉽게 실천하도록 돕는 친절한 가이드 'Green mAlt'입니다.",
			"규칙:",
			"1. 환경 보호, 재활용, 분리수거, 친환경 생활/제품, 자원순환 정책, 폐기물 처리 등의 환경 주제에만 답변합니다.",
			"2. 환경과 무관한 질문에는 정확히 다음 문장만 답합니다: 환경 관련 질문만 답변드릴 수 있습니다",
			"3. 답변은 순수 텍스트로만 작성합니다. 마크다운과 서식 기호(예: *, **, -, #, >, `, [], (), 번호 목록)를 사용하지 않습니다.",
			"4. 출력에 \\n 같은 이스케이프 문자열을 넣지 말고 실제 줄바꿈을 사용합니다. 이모지와 특수문자도 사용하지 않습니다.",
			"5. 답변은 간결하고 실용적으로, 2~5문장 범위에서 핵심만 전달합니다. 지역별 기준이 다를 수 있으면 확인을 권고합니다.",
			"6. 한국어로만 답변합니다. 모호하거나 정보가 부족하면 추가 확인이 필요하다고 말하고, 가능한 범위에서 실천 팁을 제공합니다.",
			"7. 링크, 표, 목록, 코드, 인용구를 출력하지 않습니다."
		);
	}

	private String buildConversation(List<ChatMessage> history) {
		if (history == null || history.isEmpty())
			return "";
		List<ChatMessage> sorted = history.stream()
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(ChatMessage::getCreatedAt))
			.collect(Collectors.toList());

		int fromIndex = Math.max(0, sorted.size() - MAX_CONTEXT_MESSAGES);
		List<ChatMessage> recent = sorted.subList(fromIndex, sorted.size());

		List<String> lines = new ArrayList<>();
		for (ChatMessage m : recent) {
			String prefix = m.getType() == ChatMessage.MessageType.USER ? "사용자: " : "어시스턴트: ";
			String content = Optional.ofNullable(m.getContent()).orElse("")
				.replace("\r", " ")
				.replace("\n\n", "\n")
				.trim();
			lines.add(prefix + content);
		}
		return String.join("\n", lines);
	}

	private String extractPrimaryText(GeminiResponse res) {
		if (res == null || res.getCandidates() == null || res.getCandidates().isEmpty()) {
			throw new GeminiApiFailException("빈 응답(candidates 없음)");
		}
		GeminiResponse.Candidate first = res.getCandidates().get(0);
		if (first.getContent() == null || first.getContent().getParts() == null) {
			throw new GeminiApiFailException("응답에 content.parts 없음");
		}
		if (first.getFinishReason() != null && first.getFinishReason().equalsIgnoreCase("SAFETY")) {
			throw new GeminiApiFailException("Safety 차단됨");
		}
		String text = first.getContent().getParts().stream()
			.map(GeminiResponse.Part::getText)
			.filter(Objects::nonNull)
			.collect(Collectors.joining("\n"))
			.trim();
		if (text.isBlank()) {
			throw new GeminiApiFailException("텍스트 후보 없음");
		}
		return text;
	}
}
