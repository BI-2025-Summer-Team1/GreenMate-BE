package kr.bi.greenmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpResponse {
    private Long userId;
    private String message;
}
