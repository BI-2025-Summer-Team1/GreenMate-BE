package kr.bi.greenmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "닉네임 중복 확인 응답", example = "{\"duplicate\": true}")
@Getter
@AllArgsConstructor
public class NicknameDuplicateCheckResponse {

    @Schema(description = "닉네임이 이미 사용중인지 여부", example = "true")
    private final boolean duplicate;
}
