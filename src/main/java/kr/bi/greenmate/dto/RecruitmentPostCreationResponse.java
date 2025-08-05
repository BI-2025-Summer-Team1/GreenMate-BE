package kr.bi.greenmate.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentPostCreationResponse {
    private Long postId;
    private String title;
    private String content; 
    private String authorNickname; 
    private LocalDateTime createdAt;
    private List<String> imageUrls; 
}
