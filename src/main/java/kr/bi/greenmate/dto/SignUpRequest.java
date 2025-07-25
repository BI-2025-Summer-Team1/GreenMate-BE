package kr.bi.greenmate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 60, message = "비밀번호는 8~60자여야 합니다.")
    private String password;

    private MultipartFile profileImage;

    @NotBlank(message = "자기소개는 필수입니다.")
    @Size(max = 300, message = "자기소개는 300자 이하여야 합니다.")
    private String selfIntroduction;
}
