package kr.bi.greenmate.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.OptimisticLockException;
import kr.bi.greenmate.dto.RecruitmentPostCommentRequest;
import kr.bi.greenmate.dto.RecruitmentPostCommentResponse;
import kr.bi.greenmate.dto.RecruitmentPostCreationRequest;
import kr.bi.greenmate.dto.RecruitmentPostCreationResponse;
import kr.bi.greenmate.dto.RecruitmentPostDetailResponse;
import kr.bi.greenmate.dto.RecruitmentPostLikeResponse;
import kr.bi.greenmate.dto.RecruitmentPostListResponse;
import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.entity.RecruitmentPostComment;
import kr.bi.greenmate.entity.RecruitmentPostImage;
import kr.bi.greenmate.entity.RecruitmentPostLike;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.AccessDeniedException;
import kr.bi.greenmate.exception.error.CommentNotFoundException;
import kr.bi.greenmate.exception.error.FileUploadFailException;
import kr.bi.greenmate.exception.error.ParentCommentMismatchException;
import kr.bi.greenmate.exception.error.RecruitmentPostNotFoundException;
import kr.bi.greenmate.exception.error.UserNotFoundException;
import kr.bi.greenmate.repository.ObjectStorageRepository;
import kr.bi.greenmate.repository.RecruitmentPostCommentRepository;
import kr.bi.greenmate.repository.RecruitmentPostImageRepository;
import kr.bi.greenmate.repository.RecruitmentPostLikeRepository;
import kr.bi.greenmate.repository.RecruitmentPostRepository;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecruitmentPostService {
    
    private final RecruitmentPostRepository recruitmentPostRepository;
    private final RecruitmentPostImageRepository recruitmentPostImageRepository;
    private final ObjectStorageRepository objectStorageRepository;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final RecruitmentPostLikeRepository recruitmentPostLikeRepository;
    private final RecruitmentPostCommentRepository recruitmentPostCommentRepository;
    private final RecruitmentPostViewCountService recruitmentPostViewCountService;

    public RecruitmentPostCreationResponse createRecruitmentPost(
		RecruitmentPostCreationRequest request, List<MultipartFile> images, Long userId) {

        User creator = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

        RecruitmentPost post = RecruitmentPost.builder()
			.user(creator)
			.title(request.getTitle())
			.content(request.getContent())
			.activityDate(request.getActivityDate())
			.recruitmentEndDate(request.getRecruitmentEndDate())
			.build();

        List<String> imageUrls = null;
        if (images != null && !images.isEmpty()) {
            imageUrls = images.stream()
				.map(file -> imageUploadService.upload(file, "recruitment-post"))
				.collect(Collectors.toList());

            List<RecruitmentPostImage> postImages = imageUrls.stream()
				.map(url -> RecruitmentPostImage.builder()
					.imageUrl(url)
					.recruitmentPost(post)
					.build())
				.collect(Collectors.toList());
            post.getImages().addAll(postImages);
        }

        RecruitmentPost savedPost = recruitmentPostRepository.save(post);

        return RecruitmentPostCreationResponse.builder()
			.postId(savedPost.getId())
			.title(savedPost.getTitle())
			.createdAt(savedPost.getCreatedAt())
			.build();
    }

    @Transactional
    public void deleteRecruitmentPost(Long postId, Long userId) {
        RecruitmentPost post = recruitmentPostRepository.findByIdWithUser(postId)
			.orElseThrow(() -> new RecruitmentPostNotFoundException(postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        recruitmentPostLikeRepository.deleteByRecruitmentPostId(postId);
        recruitmentPostCommentRepository.deleteByRecruitmentPostId(postId);
        recruitmentPostRepository.delete(post);

        List<RecruitmentPostImage> images = recruitmentPostImageRepository.findByRecruitmentPostId(postId);

        deleteImagesFromObjectStorage(images);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteImagesFromObjectStorage(List<RecruitmentPostImage> images) {
        images.forEach(image -> {
            try {
                objectStorageRepository.delete(image.getImageUrl());
            } catch (Exception e) {
                log.error("Failed to delete file from object storage: {}", image.getImageUrl(), e);
            }
        });
    }

    @Transactional(readOnly = true)
    public Page<RecruitmentPostListResponse> getPostList(Pageable pageable) {
        return recruitmentPostRepository.findAllWithUser(pageable)
                .map(post -> RecruitmentPostListResponse.builder()
					.postId(post.getId())
					.title(post.getTitle())
					.authorNickname(post.getUser().getNickname())
					.activityDate(post.getActivityDate())
					.createdAt(post.getCreatedAt())
					.build());
    }

    @Transactional(readOnly = true)
    public RecruitmentPostDetailResponse getPostDetail(Long postId) {
        recruitmentPostViewCountService.increment(postId);
        
        RecruitmentPost post = recruitmentPostRepository.findByIdWithUser(postId)
			.orElseThrow(() -> new RecruitmentPostNotFoundException(postId));

        List<String> imageUrls = recruitmentPostImageRepository.findByRecruitmentPostId(postId).stream()
			.map(RecruitmentPostImage::getImageUrl)
			.map(objectStorageRepository::getDownloadUrl)
			.collect(Collectors.toList());

        return RecruitmentPostDetailResponse.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.authorNickname(post.getUser().getNickname())
			.activityDate(post.getActivityDate())
			.recruitmentEndDate(post.getRecruitmentEndDate())
			.createdAt(post.getCreatedAt())
			.imageUrls(imageUrls)
			.build();
    }

    @Transactional
    @Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50)
    )
    public RecruitmentPostLikeResponse toggleLike(Long postId, Long userId) {
        RecruitmentPost post = recruitmentPostRepository.findById(postId)
			.orElseThrow(() -> new RecruitmentPostNotFoundException(postId));

        User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

        Optional<RecruitmentPostLike> existingLike = recruitmentPostLikeRepository
			.findByUser_IdAndRecruitmentPost_Id(user.getId(), postId);
        
        boolean isLiked;
        if (existingLike.isPresent()) {
            unlikePost(existingLike.get(), post);
            isLiked = false;
        } else {
            likePost(user, post);
            isLiked = true;
        }
        
        return buildLikeResponse(isLiked, post);
    }
    
    private void likePost(User user, RecruitmentPost post) {
        RecruitmentPostLike like = RecruitmentPostLike.builder()
            .user(user)
            .recruitmentPost(post)
            .build();
            
        recruitmentPostLikeRepository.save(like);
        post.increaseLikeCount();
    }

    private void unlikePost(RecruitmentPostLike existingLike, RecruitmentPost post) {
        recruitmentPostLikeRepository.delete(existingLike);
        post.decreaseLikeCount();
    }
    
    private RecruitmentPostLikeResponse buildLikeResponse(boolean isLiked, RecruitmentPost post) {
        return RecruitmentPostLikeResponse.builder()
			.liked(isLiked)
			.likeCount(post.getLikeCount())
			.build();
    }            
    
    public RecruitmentPostCommentResponse createComment(
        Long recruitmentPostId, Long userId, RecruitmentPostCommentRequest request, MultipartFile image) {

        RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(recruitmentPostId)
			.orElseThrow(() -> new RecruitmentPostNotFoundException(recruitmentPostId));
      
        User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
        
        RecruitmentPostComment parentComment = null;
        if (request.getParentCommentId() != null) {
            Optional<Long> parentCommentIdOptional = Optional.ofNullable(request.getParentCommentId());
            parentComment = recruitmentPostCommentRepository.findById(parentCommentIdOptional.get())
				.orElseThrow(CommentNotFoundException::new);

            if (!parentComment.getRecruitmentPost().getId().equals(recruitmentPostId)) {
                throw new ParentCommentMismatchException();
            }
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = imageUploadService.upload(image, "recruitment-comment");
            } catch (Exception e) {
                throw new FileUploadFailException();
            }
        }

        RecruitmentPostComment recruitmentPostComment = RecruitmentPostComment.builder()
			.recruitmentPost(recruitmentPost)
			.user(user)
			.content(request.getContent())
			.imageUrl(imageUrl)
			.parentComment(parentComment)
			.build();

        recruitmentPostCommentRepository.save(recruitmentPostComment);

        recruitmentPost.increaseCommentCount();

        return RecruitmentPostCommentResponse.builder()
			.id(recruitmentPostComment.getId())
			.userId(user.getId())
			.nickname(user.getNickname())
			.content(recruitmentPostComment.getContent())
			.createdAt(recruitmentPostComment.getCreatedAt())
			.build();
    }

    @Transactional
    @Retryable(
        retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 50)
    )
    public void deleteComment(Long commentId, Long userId) {
        RecruitmentPostComment comment = recruitmentPostCommentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException());

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        List<RecruitmentPostComment> replies = recruitmentPostCommentRepository.findByParentCommentIdIn(Collections.singletonList(commentId));
        if (replies.isEmpty()) {
            comment.getRecruitmentPost().decreaseCommentCount();
            recruitmentPostCommentRepository.delete(comment);
        } else {
            comment.setContent("삭제된 댓글입니다.");
            comment.setImageUrl(null); 
        }
    }
  
    @Transactional(readOnly = true)
    public Slice<RecruitmentPostCommentResponse> getComments(Long postId, Long lastId, int size) {
        Slice<RecruitmentPostComment> topLevelCommentsPage;

        Pageable pageable = Pageable.ofSize(size);


        if (lastId == null) {
            topLevelCommentsPage =
            recruitmentPostCommentRepository.findByRecruitmentPost_IdAndParentCommentIsNullOrderByIdDesc(postId, pageable);
        } else {
            topLevelCommentsPage =
            recruitmentPostCommentRepository.findByRecruitmentPost_IdAndParentCommentIsNullAndIdLessThanOrderByIdDesc(postId, lastId, pageable);
        }

        if (topLevelCommentsPage.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        List<Long> topCommentIds = topLevelCommentsPage.getContent().stream()
            .map(RecruitmentPostComment::getId)
            .collect(Collectors.toList());

        List<RecruitmentPostComment> allReplies = recruitmentPostCommentRepository.findByParentCommentIdIn(topCommentIds);

        Map<Long, List<RecruitmentPostComment>> repliesByParentId = allReplies.stream()
            .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));

        return topLevelCommentsPage.map(topComment -> {
        List<RecruitmentPostComment> replies = repliesByParentId.getOrDefault(topComment.getId(), Collections.emptyList());

        return mapToCommentResponse(topComment, replies);
        });
    }

    private RecruitmentPostCommentResponse mapToCommentResponse(
        RecruitmentPostComment comment, List<RecruitmentPostComment> replies) {

        List<RecruitmentPostCommentResponse> replyResponses = replies.stream()  
            .map(reply -> mapToCommentResponse(reply, Collections.emptyList()))  
            .collect(Collectors.toList());  

        return RecruitmentPostCommentResponse.builder()
            .id(comment.getId())
            .userId(comment.getUser().getId())
            .nickname(comment.getUser().getNickname())
            .content(comment.getContent())
            .imageUrl(comment.getImageUrl() != null ? objectStorageRepository.getDownloadUrl(comment.getImageUrl()) : null)
            .createdAt(comment.getCreatedAt())
            .replies(replyResponses)
            .build();
    }
}
