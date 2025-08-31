package kr.bi.greenmate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.bi.greenmate.dto.LoginRequest;
import kr.bi.greenmate.dto.LoginResponse;
import kr.bi.greenmate.dto.NicknameDuplicateCheckResponse;
import kr.bi.greenmate.dto.SignUpRequest;
import kr.bi.greenmate.dto.SignUpResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

	private final AuthService authService;

	@PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "회원가입",
		description = "새로운 사용자를 등록합니다."
	)
	public ResponseEntity<SignUpResponse> signUp(
		@RequestPart("request") @Valid SignUpRequest request,
		@Parameter(description = "프로필 이미지 파일 (선택사항)", example = "profile.jpg")
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage
	) {
		SignUpResponse response = authService.signUp(request, profileImage);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/check-nickname")
	@Operation(summary = "닉네임 중복 확인", description = "닉네임의 중복을 확인합니다.")
	public ResponseEntity<NicknameDuplicateCheckResponse> checkNicknameDuplicate(
		@RequestParam String nickname
	) {
		boolean isDuplicate = authService.isNicknameDuplicate(nickname);
		NicknameDuplicateCheckResponse response = new NicknameDuplicateCheckResponse(isDuplicate);
		return ResponseEntity.ok(response);

	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
	public ResponseEntity<LoginResponse> login(
		@Valid @RequestBody LoginRequest request
	) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	@Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 소프트 삭제합니다.")
	public ResponseEntity<Void> deleteMe(
		@AuthenticationPrincipal User user) {
		authService.deleteUser(user);
		return ResponseEntity.noContent().build();
	}
}
