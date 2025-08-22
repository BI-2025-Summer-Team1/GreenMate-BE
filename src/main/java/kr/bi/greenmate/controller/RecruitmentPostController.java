package kr.bi.greenmate.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.bi.greenmate.dto.RecruitmentPostCreationRequest;
import kr.bi.greenmate.dto.RecruitmentPostCreationResponse;
import kr.bi.greenmate.dto.RecruitmentPostDetailResponse;
import kr.bi.greenmate.dto.RecruitmentPostLikeResponse;
import kr.bi.greenmate.dto.RecruitmentPostListResponse;
import kr.bi.greenmate.service.RecruitmentPostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/recruitment-posts")
@RequiredArgsConstructor
@Tag(name = "Recruitment Post API", description = "환경활동 모집글 관련 API")
public class RecruitmentPostController {

    private final RecruitmentPostService recruitmentPostService;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "모집글 생성", description = "새로운 환경활동 모집글을 생성합니다.")
    public ResponseEntity<RecruitmentPostCreationResponse> createPost(
            @RequestPart @Valid RecruitmentPostCreationRequest request,
            @RequestPart(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal Long userId) {

        RecruitmentPostCreationResponse response = recruitmentPostService.createRecruitmentPost(request, images, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "모집글 목록 조회", description = "환경활동 모집글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentPostListResponse>> getRecruitmentPostList(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<RecruitmentPostListResponse> postList = recruitmentPostService.getPostList(pageable);

        return ResponseEntity.ok(postList);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "모집글 상세 조회", description = "특정 ID의 환경활동 모집글 상세 정보를 조회합니다.")
    public ResponseEntity<RecruitmentPostDetailResponse> getRecruitmentPostDetail(@PathVariable Long postId) {
        RecruitmentPostDetailResponse postDetail = recruitmentPostService.getPostDetail(postId);

        return ResponseEntity.ok(postDetail);
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "모집글 좋아요 토글", description = "모집글에 좋아요를 누르거나 취소합니다.")
    public ResponseEntity<RecruitmentPostLikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {

        RecruitmentPostLikeResponse response = recruitmentPostService.toggleLike(postId, userId);
        
        return ResponseEntity.ok(response);
    }
}
