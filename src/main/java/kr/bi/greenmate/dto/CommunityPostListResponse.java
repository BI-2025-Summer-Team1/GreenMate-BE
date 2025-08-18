package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 목록 조회 응답")
public class CommunityPostListResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "환경 보호에 대한 이야기")
    private String title;

    @Schema(description = "작성자 닉네임", example = "환경지킴이")
    private String authorNickname;

    @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean isLikedByUser;

    @Schema(description = "총 좋아요 수", example = "42")
    private Long likeCount;

    @Schema(description = "조회수", example = "128")
    private Long viewCount;

    @Schema(description = "댓글 수", example = "5")
    private Long commentCount;
}
