package kr.bi.greenmate.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentPostCreationRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "활동일자는 필수입니다.")
    private LocalDateTime activityDate;

    @NotNull(message = "모집 종료일은 필수입니다.")
    private LocalDateTime recruitmentEndDate;

    @Size(max = 5, message = "사진은 최대 5장까지 등록할 수 있습니다.")
    private List<String> imageUrls;
}
