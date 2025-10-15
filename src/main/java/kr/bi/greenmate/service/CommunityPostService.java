package kr.bi.greenmate.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import kr.bi.greenmate.dto.CommunityPostCommentRequest;
import kr.bi.greenmate.dto.CommunityPostCommentResponse;
import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.dto.CommunityPostDetailResponse;
import kr.bi.greenmate.dto.CommunityPostLikeResponse;
import kr.bi.greenmate.dto.CommunityPostListResponse;
import kr.bi.greenmate.dto.KeysetSliceResponse;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostComment;
import kr.bi.greenmate.entity.CommunityPostImage;
import kr.bi.greenmate.entity.CommunityPostLike;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.AccessDeniedException;
import kr.bi.greenmate.exception.error.CommentNotFoundException;
import kr.bi.greenmate.exception.error.FileUploadFailException;
import kr.bi.greenmate.exception.error.ImageCountExceedException;
import kr.bi.greenmate.exception.error.ImageSizeExceedException;
import kr.bi.greenmate.exception.error.OptimisticLockCustomException;
import kr.bi.greenmate.exception.error.ParentCommentMismatchException;
import kr.bi.greenmate.exception.error.PostNotFoundException;
import kr.bi.greenmate.repository.CommunityPostCommentRepository;
import kr.bi.greenmate.repository.CommunityPostImageRepository;
import kr.bi.greenmate.repository.CommunityPostLikeRepository;
import kr.bi.greenmate.repository.CommunityPostRepository;
import kr.bi.greenmate.repository.ObjectStorageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
	private final CommunityPostRepository communityPostRepository;
	private final CommunityPostLikeRepository communityPostLikeRepository;
	private final CommunityPostImageRepository communityPostImageRepository;
	private final ObjectStorageRepository objectStorageRepository;
	private final ImageUploadService imageUploadService;
	private final ViewCountService viewCountService;
	private final CommentCreationService commentCreationService;
	private final CommunityPostCommentRepository communityPostCommentRepository;
	private final UserDisplayService userDisplayService;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public CommunityPostCreateResponse createPost(User user, CommunityPostCreateRequest request,
		List<MultipartFile> images) {
		CommunityPost post = CommunityPost.builder()
			.user(user)
			.title(request.getTitle())
			.content(request.getContent())
			.build();

		final long MAX_IMAGE_SIZE = 1024 * 1024;
		if (images != null && !images.isEmpty()) {
			if (images.size() >= 10)
				throw new ImageCountExceedException();
			for (MultipartFile image : images) {
				if (image.getSize() > MAX_IMAGE_SIZE) {
					throw new ImageSizeExceedException();
				}
			}
			List<CommunityPostImage> imageEntities = images.stream()
				.map(image -> {
					String imageUrl = imageUploadService.upload(image, "community");
					return CommunityPostImage.builder()
						.communityPost(post)
						.imageUrl(imageUrl)
						.build();
				}).toList();

			post.getImages().addAll(imageEntities);
		}

		CommunityPost savedPost = communityPostRepository.save(post);

		return new CommunityPostCreateResponse(savedPost.getId());
	}

	@Transactional
	public CommunityPostLikeResponse toggleLike(Long postId, User user) {
		int maxRetry = 3;
		int retryCount = 0;

		while (retryCount < maxRetry) {
			try {
				return doToggleLike(postId, user);
			} catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
				if (++retryCount >= maxRetry) {
					throw new OptimisticLockCustomException();
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			}
		}
		throw new OptimisticLockCustomException();
	}

	private CommunityPostLikeResponse unlikePost(CommunityPostLike existingLike, CommunityPost post) {
		communityPostLikeRepository.delete(existingLike);
		post.decrementLikeCount();

		return buildLikeResponse(false, post);
	}

	private CommunityPostLikeResponse likePost(User user, CommunityPost post) {
		CommunityPostLike like = CommunityPostLike.builder()
			.user(user)
			.communityPost(post)
			.build();

		communityPostLikeRepository.save(like);
		post.incrementLikeCount();

		return buildLikeResponse(true, post);
	}

	private CommunityPostLikeResponse buildLikeResponse(boolean isLiked, CommunityPost post) {
		return CommunityPostLikeResponse.builder()
			.isLiked(isLiked)
			.likeCount(post.getLikeCount())
			.build();
	}

	private CommunityPostLikeResponse doToggleLike(Long postId, User user) {
		CommunityPost post = communityPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		Optional<CommunityPostLike> existingLike = communityPostLikeRepository
			.findByUserIdAndCommunityPostId(user.getId(), postId);

		if (existingLike.isPresent()) {
			return unlikePost(existingLike.get(), post);
		} else {
			return likePost(user, post);
		}
	}

	@Transactional(readOnly = true)
	public CommunityPostDetailResponse getPost(Long postId, User user) {

		CommunityPost post = communityPostRepository.findByIdWithUserAndImages(postId)
			.orElseThrow(PostNotFoundException::new);

		viewCountService.increment(postId);

		List<String> imageUrls = communityPostImageRepository.findImageUrlsByPostId(postId).stream()
			.map(objectStorageRepository::getDownloadUrl)
			.collect(Collectors.toList());

		Boolean isLikedByUser = communityPostLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);

		long displayViewCount = post.getViewCount() + viewCountService.getDelta(postId);

		return CommunityPostDetailResponse.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.imageUrls(imageUrls)
			.authorNickname(userDisplayService.displayName(post.getUser()))
			.isLikedByUser(isLikedByUser)
			.likeCount(post.getLikeCount())
			.viewCount(displayViewCount)
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	@Transactional(readOnly = true)
	public KeysetSliceResponse<CommunityPostListResponse> getPosts(User user, Long lastPostId, int size) {

		Pageable pageable = PageRequest.of(0, size);

		Slice<CommunityPost> slice;
		if (lastPostId == null) {
			slice = communityPostRepository.findFirstPage(pageable);
		} else {
			slice = communityPostRepository.findNextPage(lastPostId, pageable);
		}

		List<CommunityPost> posts = slice.getContent();

		Set<Long> likedPostIds;
		if (user != null && !posts.isEmpty()) {
			likedPostIds = new HashSet<>(
				communityPostLikeRepository.findLikedPostIdsByUserIdAndPosts(user.getId(), posts));
		} else {
			likedPostIds = Collections.emptySet();
		}

		List<CommunityPostListResponse> content = posts.stream()
			.map(post -> CommunityPostListResponse.builder()
				.postId(post.getId())
				.title(post.getTitle())
				.authorNickname(userDisplayService.displayName(post.getUser()))
				.createdAt(post.getCreatedAt())
				.isLikedByUser(likedPostIds.contains(post.getId()))
				.likeCount(post.getLikeCount())
				.viewCount(post.getViewCount())
				.commentCount(post.getCommentCount())
				.build())
			.toList();

		Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).getPostId();

		return new KeysetSliceResponse<>(content, slice.hasNext(), newLastId);
	}

	public CommunityPostCommentResponse createComment(Long postId, User user,
		CommunityPostCommentRequest request, MultipartFile image) {

		String imageUrl = processCommentImage(image);

		return commentCreationService.createCommentInTransaction(postId, user, request, imageUrl);
	}

	private String processCommentImage(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return null;
		}

		final long MAX_IMAGE_SIZE = 1024 * 1024;
		if (image.getSize() > MAX_IMAGE_SIZE) {
			throw new ImageSizeExceedException();
		}

		try {
			return imageUploadService.upload(image, "community-comment");
		} catch (RuntimeException e) {
			throw new FileUploadFailException();
		}
	}

	private CommunityPostCommentResponse doCreateComment(Long postId, User user,
		CommunityPostCommentRequest request, String imageUrl) {

		CommunityPost post = communityPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		CommunityPostComment parentComment = validateParentComment(request.getParentCommentId(), postId);

		CommunityPostComment comment = CommunityPostComment.builder()
			.parent(post)
			.user(user)
			.content(request.getContent())
			.imageUrl(imageUrl)
			.communityPostComment(parentComment)
			.build();

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
			.nickname(userDisplayService.displayName(comment.getUser()))
			.content(comment.getContent())
			.imageUrl(comment.getImageUrl() == null ? null
				: objectStorageRepository.getDownloadUrl(comment.getImageUrl()))
			.createdAt(comment.getCreatedAt())
			.build();
	}

	@Transactional(readOnly = true)
	public KeysetSliceResponse<CommunityPostCommentResponse> getComments(Long postId, Long lastCommentId, int size) {

		if (!communityPostRepository.existsById(postId)) {
			throw new PostNotFoundException();
		}

		Pageable pageable = PageRequest.of(0, size + 1);
		List<CommunityPostComment> results;

		if (lastCommentId == null) {
			results = communityPostCommentRepository.findByParent_IdOrderByIdDesc(postId, pageable);
		} else {
			results = communityPostCommentRepository.findByParent_IdAndIdLessThanOrderByIdDesc(postId, lastCommentId,
				pageable);
		}

		boolean hasNext = results.size() > size;
		List<CommunityPostCommentResponse> content = results.stream()
			.limit(size)
			.map(this::buildCommentResponse)
			.toList();

		Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).getId();

		return new KeysetSliceResponse<>(content, hasNext, newLastId);
	}

	@Transactional
	@Retryable(
		retryFor = {
			PessimisticLockException.class,
			LockTimeoutException.class,
			PessimisticLockingFailureException.class
		},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50)
	)
	public void deletePost(Long postId, User user) {
		CommunityPost post = communityPostRepository.findWithLockById(postId)
			.orElseThrow(PostNotFoundException::new);

		if (!post.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException();
		}

		List<String> postImageKeys = communityPostImageRepository.findImageUrlsByPostId(postId);
		List<String> commentImageKeys = communityPostCommentRepository.findImageUrlsByPostId(postId);
		List<String> allImageKeys = Stream.concat(
			postImageKeys.stream(),
			commentImageKeys.stream()
		).collect(Collectors.toList());

		communityPostCommentRepository.deleteByParent_IdAndCommunityPostCommentIsNotNull(postId);
		communityPostCommentRepository.deleteByParent_IdAndCommunityPostCommentIsNull(postId);
		communityPostLikeRepository.deleteByCommunityPostId(postId);
		communityPostImageRepository.deleteByCommunityPostId(postId);
		communityPostRepository.delete(post);

		if (!allImageKeys.isEmpty()) {
			publisher.publishEvent(new ImagesToDeleteEvent(allImageKeys));
		}
	}

	@Transactional
	@Retryable(
		retryFor = {
			PessimisticLockException.class,
			LockTimeoutException.class,
			OptimisticLockException.class,
			ObjectOptimisticLockingFailureException.class
		},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50)
	)
	public void deleteComment(Long commentId, User user) {

		CommunityPostComment comment = communityPostCommentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);

		if (!comment.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException();
		}

		if (comment.isDeleted()) {
			return;
		}

		String imageKeyToDelete = comment.getImageUrl();
		comment.getParent().decrementCommentCount();
		comment.markAsDeleted();

		if (imageKeyToDelete != null) {
			publisher.publishEvent(new ImagesToDeleteEvent(Collections.singletonList(imageKeyToDelete)));
		}
	}

	@Transactional(readOnly = true)
	public KeysetSliceResponse<CommunityPostListResponse> getUserPosts(User currentUser, Long userId, Long lastPostId,
		int size) {

		Pageable pageable = PageRequest.of(0, size);

		Slice<CommunityPost> slice;
		if (lastPostId == null) {
			slice = communityPostRepository.findByUserIdOrderByIdDesc(userId, pageable);
		} else {
			slice = communityPostRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, lastPostId, pageable);
		}

		List<CommunityPost> posts = slice.getContent();

		Set<Long> likedPostIds;
		if (currentUser != null && !posts.isEmpty()) {
			likedPostIds = new HashSet<>(
				communityPostLikeRepository.findLikedPostIdsByUserIdAndPosts(currentUser.getId(), posts));
		} else {
			likedPostIds = Collections.emptySet();
		}

		List<CommunityPostListResponse> content = posts.stream()
			.map(post -> CommunityPostListResponse.builder()
				.postId(post.getId())
				.title(post.getTitle())
				.authorNickname(userDisplayService.displayName(post.getUser()))
				.createdAt(post.getCreatedAt())
				.isLikedByUser(likedPostIds.contains(post.getId()))
				.likeCount(post.getLikeCount())
				.viewCount(post.getViewCount())
				.commentCount(post.getCommentCount())
				.build())
			.toList();

		Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).getPostId();

		return new KeysetSliceResponse<>(content, slice.hasNext(), newLastId);
	}
}
