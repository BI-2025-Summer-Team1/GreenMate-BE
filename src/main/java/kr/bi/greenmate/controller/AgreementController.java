package kr.bi.greenmate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.bi.greenmate.dto.AgreementsResponse;
import kr.bi.greenmate.service.AgreementService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/agreements")
@RequiredArgsConstructor
@Tag(name = "Agreement API", description = "약관 관련 API")
public class AgreementController {

	private final AgreementService agreementService;

	@GetMapping
	@Operation(summary = "전체 약관 조회",
		description = "회원가입 시 필요한 전체 약관 목록을 조회합니다. 필수 및 선택 약관이 하나의 리스트로 제공되며, 각 약관 객체의 isRequired 필드를 통해 구분할 수 있습니다.")
	public ResponseEntity<AgreementsResponse> getAllAgreements() {
		AgreementsResponse agreements = agreementService.getAllAgreements();
		return ResponseEntity.ok(agreements);
	}
}
