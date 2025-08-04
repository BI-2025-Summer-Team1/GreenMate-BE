package kr.bi.greenmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostCreateRequest {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
    private String content;

    @Builder.Default
    @Setter
    @Size(max = 10, message = "이미지는 10개 이하여야 합니다.")
    private List<MultipartFile> images = List.of();
}
