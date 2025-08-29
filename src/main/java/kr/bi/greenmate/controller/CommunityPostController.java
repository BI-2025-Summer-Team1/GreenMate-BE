package kr.bi.greenmate.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import kr.bi.greenmate.dto.CommunityPostCommentRequest;
import kr.bi.greenmate.dto.CommunityPostCommentResponse;
import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.dto.CommunityPostDetailResponse;
import kr.bi.greenmate.dto.CommunityPostLikeResponse;
import kr.bi.greenmate.dto.CommunityPostListResponse;
import kr.bi.greenmate.dto.KeysetSliceResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.service.CommunityPostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "Community API", description = "커뮤니티 관련 API")
public class CommunityPostController {
	private final CommunityPostService communityPostService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "커뮤니티 글 작성",
		description = "새로운 글을 등록합니다."
	)
	public ResponseEntity<CommunityPostCreateResponse> createPost(
		@AuthenticationPrincipal User user,
		@RequestPart("request") @Valid CommunityPostCreateRequest request,
		@Parameter(description = "게시글에 첨부할 이미지 파일들 (최대 10개, 선택사항)", example = "image1.jpg, image2.png")
		@RequestPart(value = "images", required = false) @Size(max = 10) List<MultipartFile> images) {
		CommunityPostCreateResponse response = communityPostService.createPost(user, request, images);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{postId}/like")
	@Operation(summary = "좋아요 토글", description = "게시글에 좋아요를 추가하거나 취소합니다.")
	public ResponseEntity<CommunityPostLikeResponse> toggleLike(
		@AuthenticationPrincipal User user,
		@Parameter(description = "좋아요를 토글할 게시글 ID", example = "123")
		@PathVariable long postId) {
		CommunityPostLikeResponse response = communityPostService.toggleLike(postId, user);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(summary = "커뮤니티 글 목록 조회", description = "커뮤니티 글 목록을 조회합니다.")
	public ResponseEntity<KeysetSliceResponse<CommunityPostListResponse>> getPosts(
		@AuthenticationPrincipal User user,
		@Parameter(description = "마지막으로 조회한 게시글 ID (첫 페이지 조회 시 생략 가능)", example = "100")
		@RequestParam(required = false) Long lastPostId,
		@Parameter(description = "한 번에 조회할 게시글 개수", example = "10")
		@RequestParam(defaultValue = "10") int size) {
		KeysetSliceResponse<CommunityPostListResponse> response = communityPostService.getPosts(user, lastPostId, size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{postId}")
	@Operation(summary = "커뮤니티 글 상세 조회", description = "글의 상세 정보를 조회합니다.")
	public ResponseEntity<CommunityPostDetailResponse> getPost(
		@Parameter(description = "조회할 게시글 ID", example = "123")
		@PathVariable long postId,
		@AuthenticationPrincipal User user) {
		CommunityPostDetailResponse response = communityPostService.getPost(postId, user);

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/{postId}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "커뮤니티 댓글 작성", description = "특정 커뮤니티 글에 댓글을 작성합니다.")
	public ResponseEntity<CommunityPostCommentResponse> createComment(
		@Parameter(description = "댓글을 작성할 커뮤니티 글 ID", example = "123")
		@PathVariable long postId,
		@AuthenticationPrincipal User user,
		@Parameter(description = "댓글 작성 요청 본문", required = true)
		@RequestPart("request") @Valid CommunityPostCommentRequest request,
		@Parameter(description = "첨부 이미지 (최대 1개)", example = "comment.jpg")
		@RequestPart(value = "image", required = false) MultipartFile image) {

		CommunityPostCommentResponse response =
			communityPostService.createComment(postId, user, request, image);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{postId}/comments")
	@Operation(summary = "커뮤니티 글 댓글 목록 조회", description = "특정 커뮤니티 글의 댓글 목록을 무한 스크롤로 조회합니다.")
	public ResponseEntity<KeysetSliceResponse<CommunityPostCommentResponse>> getComments(
		@Parameter(description = "댓글을 조회할 커뮤니티 글 ID", example = "123")
		@PathVariable long postId,
		@Parameter(description = "마지막으로 조회한 댓글 ID (첫 페이지면 생략)", example = "100")
		@RequestParam(required = false) Long lastCommentId,
		@Parameter(description = "한 번에 조회할 댓글 수", example = "10")
		@RequestParam(defaultValue = "10") int size) {

		KeysetSliceResponse<CommunityPostCommentResponse> response =
			communityPostService.getComments(postId, lastCommentId, size);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{postId}/comments/{commentId}")
	@Operation(summary = "커뮤니티 댓글 삭제", description = "특정 커뮤니티 글의 댓글을 삭제합니다.")
	public ResponseEntity<Void> deleteComment(
		@Parameter(description = "댓글을 삭제할 커뮤니티 글 ID", example = "123")
		@PathVariable long postId,
		@Parameter(description = "삭제할 댓글 ID", example = "456")
		@PathVariable long commentId,
		@AuthenticationPrincipal User user) {

		communityPostService.deleteComment(postId, commentId, user);
		return ResponseEntity.noContent().build();
	}
}
