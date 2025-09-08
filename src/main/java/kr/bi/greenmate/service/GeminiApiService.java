package kr.bi.greenmate.service;

import kr.bi.greenmate.entity.ChatMessage;
import kr.bi.greenmate.exception.error.GeminiApiFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final WebClient webClient;

	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public String generateResponse(List<ChatMessage> history) {
		try {
			StringBuilder prompt = new StringBuilder();
			prompt.append("당신은 사용자가 환경 보호를 쉽게 실천하도록 돕는 친절한 가이드 'Green mAlt'입니다.\n");
			prompt.append("규칙:\n");
			prompt.append("1. 환경 보호, 재활용, 분리수거, 친환경 생활/제품, 자원순환 정책, 폐기물 처리 등의 환경 주제에만 답변합니다.\n");
			prompt.append("2. 환경과 무관한 질문에는 정확히 다음 문장만 답합니다: 환경 관련 질문만 답변드릴 수 있습니다\n");
			prompt.append("3. 답변은 순수 텍스트로만 작성합니다. 마크다운과 서식 기호(예: *, **, -, #, >, `, [], (), 번호 목록)를 사용하지 않습니다.\n");
			prompt.append("4. 출력에 \\n 같은 이스케이프 문자열을 넣지 말고 실제 줄바꿈을 사용합니다. 이모지와 특수문자도 사용하지 않습니다.\n");
			prompt.append("5. 답변은 간결하고 실용적으로, 2~5문장 범위에서 핵심만 전달합니다. 지역별 기준이 다를 수 있으면 확인을 권고합니다.\n");
			prompt.append("6. 한국어로만 답변합니다. 모호하거나 정보가 부족하면 추가 확인이 필요하다고 말하고, 가능한 범위에서 실천 팁을 제공합니다.\n");
			prompt.append("7. 링크, 표, 목록, 코드, 인용구를 출력하지 않습니다.\n");
			prompt.append("\n");
			prompt.append("예시:\n");
			prompt.append("사용자: 배달용 플라스틱 용기는 어떻게 버려야 하나요?\n");
			prompt.append(
				"어시스턴트: 남은 음식물을 비우고 물로 한번 헹군 뒤 라벨과 이물질을 제거합니다. 깨끗하게 비워지면 플라스틱류로 분리 배출할 수 있습니다. 기름때가 심해 세척이 어려우면 일반 쓰레기로 버리는 편이 안전합니다. 거주 지역의 세부 기준을 확인해 최종 배출 방법을 결정하세요.\n");
			prompt.append("\n");
			prompt.append("사용자: 오늘 주식 시장 어때?\n");
			prompt.append("어시스턴트: 환경 관련 질문만 답변드릴 수 있습니다\n");
			prompt.append("\n");
			prompt.append("사용자: 종이 빨대가 정말 환경에 더 좋은가요?\n");
			prompt.append(
				"어시스턴트: 종이 빨대는 일반적으로 분해가 더 빠르고 재활용 체계에 더 잘 맞는 편이지만, 코팅 여부와 오염도에 따라 달라질 수 있습니다. 일회용 사용을 줄이는 것이 가장 효과적이며 가능한 경우 다회용 제품으로 대체하는 방법을 권장합니다. 지역 기준을 확인해 올바른 배출 방법을 선택하세요.\n");
			prompt.append("\n");

			String lastUserMessage = history.stream()
				.filter(msg -> msg.getType() == ChatMessage.MessageType.USER)
				.findFirst()
				.map(ChatMessage::getContent)
				.orElse("");

			prompt.append("질문: ").append(lastUserMessage);

			String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

			String requestBody = String.format("""
				{
					"contents": [{
						"parts": [{
							"text": "%s"
						}]
					}],
					"generationConfig": {
						"maxOutputTokens": 1000,
						"temperature": 0.7
					}
				}
				""", prompt.toString().replace("\"", "\\\""));

			String response = webClient.post()
				.uri(url)
				.header("Content-Type", "application/json")
				.header("X-goog-api-key", apiKey)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();

			log.info("Gemini API 응답: {}", response);
			return extractTextFromResponse(response);

		} catch (Exception e) {
			log.error("Gemini API 호출 중 오류 발생", e);
			throw new GeminiApiFailException();
		}
	}

	private String extractTextFromResponse(String response) {
		try {
			log.info("파싱할 응답: {}", response);

			if (response.contains("\"candidates\"")) {
				int textStart = response.indexOf("\"text\": \"") + 9;
				if (textStart > 8) {
					int textEnd = response.indexOf("\"", textStart);
					if (textEnd > textStart) {
						String extractedText = response.substring(textStart, textEnd);
						return extractedText.replace("\\n", " ").replace("\\\"", "\"").trim();
					}
				}
			}

			log.warn("응답에서 텍스트를 찾을 수 없습니다. 응답: {}", response);
			return "응답을 파싱할 수 없습니다.";

		} catch (Exception e) {
			log.error("응답 파싱 중 오류 발생: {}", e.getMessage(), e);
			return "응답을 파싱할 수 없습니다.";
		}
	}
}
