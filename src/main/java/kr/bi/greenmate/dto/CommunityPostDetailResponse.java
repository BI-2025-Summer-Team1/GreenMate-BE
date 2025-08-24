package kr.bi.greenmate.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 상세 조회 응답")
public class CommunityPostDetailResponse {
	@Schema(description = "게시글 ID", example = "1")
	private Long postId;

	@Schema(description = "게시글 제목", example = "환경 보호에 대한 이야기")
	private String title;

	@Schema(description = "게시글 내용", example = "오늘 환경 보호에 대한 생각을 나누고 싶습니다.")
	private String content;

	@Schema(description = "게시글 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
	private List<String> imageUrls;

	@Schema(description = "작성자 닉네임", example = "환경지킴이")
	private String authorNickname;

	@Schema(description = "현재 사용자의 좋아요 여부", example = "true")
	private Boolean isLikedByUser;

	@Schema(description = "총 좋아요 수", example = "42")
	private Long likeCount;

	@Schema(description = "조회수", example = "128")
	private Long viewCount;

	@Schema(description = "생성일시", example = "2024-01-01T10:00:00")
	private LocalDateTime createdAt;

	@Schema(description = "수정일시", example = "2024-01-01T10:00:00")
	private LocalDateTime updatedAt;
}
