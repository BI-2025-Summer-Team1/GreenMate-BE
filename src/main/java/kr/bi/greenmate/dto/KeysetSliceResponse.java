package kr.bi.greenmate.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "Keyset 기반 페이징 응답")
public class KeysetSliceResponse<T> {

	@Schema(description = "데이터 목록")
	private final List<T> content;

	@Schema(description = "다음 페이지 존재 여부")
	private final Boolean hasNext;

	@Schema(description = "다음 페이지 커서 (마지막 아이템 id)")
	private final Long lastId;
}
