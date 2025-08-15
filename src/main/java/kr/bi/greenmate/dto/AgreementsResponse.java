package kr.bi.greenmate.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgreementsResponse {

    @Schema(description = "전체 약관 목록 (필수/선택 포함)")
    private List<AgreementResponse> agreements;
}
