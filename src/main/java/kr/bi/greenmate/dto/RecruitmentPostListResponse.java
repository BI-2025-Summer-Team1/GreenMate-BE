package kr.bi.greenmate.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "모집 게시물 목록 응답 DTO")
public class RecruitmentPostListResponse {

    @Schema(description = "게시물 고유 ID", example = "101")
    private Long postId;

    @Schema(description = "게시물 제목", example = "새로운 플로깅 모임을 모집합니다!")
    private String title;

    @Schema(description = "작성자 닉네임", example = "푸른별지킴이")
    private String authorNickname;

    @Schema(description = "활동 일자", example = "2025-08-15T10:30:00")
    private LocalDateTime activityDate;

    @Schema(description = "게시물 작성 시간", example = "2025-08-05T15:30:00")
    private LocalDateTime createdAt;
}
