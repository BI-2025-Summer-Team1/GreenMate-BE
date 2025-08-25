package kr.bi.greenmate.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "모집 게시물 댓글 응답 DTO")
public class RecruitmentPostCommentResponse {

    @Schema(description = "댓글 ID")
    private Long id;

    @Schema(description = "댓글 작성자 ID")
    private Long userId;

    @Schema(description = "댓글 작성자 닉네임")
    private String nickname;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 첨부 이미지 URL")
    private String imageUrl;

    @Schema(description = "대댓글 목록")
    private List<RecruitmentPostCommentResponse> replies;
}
