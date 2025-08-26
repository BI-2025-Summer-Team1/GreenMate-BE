package kr.bi.greenmate.service;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import kr.bi.greenmate.dto.CommunityPostCommentRequest;
import kr.bi.greenmate.dto.CommunityPostCommentResponse;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostComment;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.ParentCommentMismatchException;
import kr.bi.greenmate.exception.error.PostNotFoundException;
import kr.bi.greenmate.repository.CommunityPostCommentRepository;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentCreationService {

	private final CommunityPostRepository communityPostRepository;
	private final CommunityPostCommentRepository communityPostCommentRepository;

	@Transactional
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50)
	)
	public CommunityPostCommentResponse createCommentInTransaction(Long postId, User user,
		CommunityPostCommentRequest request, String imageUrl) {

		CommunityPost post = communityPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		CommunityPostComment parent = communityPostCommentRepository
			.findByIdAndParentId(request.getParentCommentId(), postId)
			.orElseThrow(ParentCommentMismatchException::new);

		CommunityPostComment comment = CommunityPostComment.builder()
			.parent(post).user(user).content(request.getContent())
			.imageUrl(imageUrl).communityPostComment(parent).build();

		communityPostCommentRepository.save(comment);
		post.incrementCommentCount();
		return buildCommentResponse(comment);
	}

	private CommunityPostComment validateParentComment(Long parentCommentId, Long postId) {
		if (parentCommentId == null)
			return null;

		return communityPostCommentRepository
			.findByIdAndParentId(parentCommentId, postId)
			.orElseThrow(ParentCommentMismatchException::new);
	}

	private CommunityPostCommentResponse buildCommentResponse(CommunityPostComment comment) {
		return CommunityPostCommentResponse.builder()
			.id(comment.getId())
			.userId(comment.getUser().getId())
			.nickname(comment.getUser().getNickname())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
