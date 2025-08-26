package kr.bi.greenmate.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Keyset 기반 페이징 응답")
public record KeysetSliceResponse<T>(

	@Schema(description = "데이터 목록") List<T> content,

	@Schema(description = "다음 페이지 존재 여부") Boolean hasNext,

	@Schema(description = "다음 페이지 커서 (마지막 아이템 id)") Long lastId) {
}
