package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 응답")
public class SignUpResponse {
	@Schema(description = "생성된 사용자 ID", example = "1")
	private Long userId;

	@Schema(description = "응답 메시지", example = "회원가입이 성공적으로 완료되었습니다.")
	private String message;

	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	private String profileImageUrl;
}
