package kr.bi.greenmate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.bi.greenmate.dto.LoginRequest;
import kr.bi.greenmate.dto.LoginResponse;
import kr.bi.greenmate.dto.SignUpRequest;
import kr.bi.greenmate.dto.SignUpResponse;
import kr.bi.greenmate.service.LoginService;
import kr.bi.greenmate.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final SignUpService signUpService;
    private final LoginService loginService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @ModelAttribute SignUpRequest request
    ) {
        SignUpResponse response = signUpService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }
}
