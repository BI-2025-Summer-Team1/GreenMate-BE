package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class SignUpRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    @Schema(description = "사용자 이메일", example = "user@example.com", maxLength = 100)
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
    @Schema(description = "사용자 닉네임", example = "환경지킴이", maxLength = 10)
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 60, message = "비밀번호는 8~60자여야 합니다.")
    @Schema(description = "사용자 비밀번호", example = "password123", minLength = 8, maxLength = 60)
    private String password;

    @Setter
    @Schema(description = "프로필 이미지 파일")
    private MultipartFile profileImage;
}
