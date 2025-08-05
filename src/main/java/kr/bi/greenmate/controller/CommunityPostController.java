package kr.bi.greenmate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "Community API", description = "커뮤니티 관련 API")
public class CommunityPostController {
    private final CommunityPostService communityPostService;

    @PostMapping
    @Operation(summary = "커뮤니티 글 작성", description = "새로운 글을 등록합니다.")
    public ResponseEntity<CommunityPostCreateResponse> createPost(
        @AuthenticationPrincipal User user,
        @RequestPart("request") @Valid CommunityPostCreateRequest request,
        @RequestPart(value = "images", required = false) @Size(max = 10) List<MultipartFile> images)
    {
        CommunityPostCreateResponse response = communityPostService.createPost(user, request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
