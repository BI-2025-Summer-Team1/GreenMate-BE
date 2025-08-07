package kr.bi.greenmate.service;

import jakarta.persistence.OptimisticLockException;
import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.dto.CommunityPostLikeResponse;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import kr.bi.greenmate.entity.CommunityPostLike;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.ImageCountExceedException;
import kr.bi.greenmate.exception.error.ImageSizeExceedException;
import kr.bi.greenmate.exception.error.OptimisticLockCustomException;
import kr.bi.greenmate.exception.error.PostNotFoundException;
import kr.bi.greenmate.repository.CommunityPostLikeRepository;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostLikeRepository communityPostLikeRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public CommunityPostCreateResponse createPost(User user, CommunityPostCreateRequest request, List<MultipartFile> images){
        CommunityPost post = CommunityPost.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        final long MAX_IMAGE_SIZE = 1024 * 1024;
        if(images != null && !images.isEmpty()){
            if(images.size() >= 10) throw new ImageCountExceedException();
            for(MultipartFile image : images){
                if(image.getSize() > MAX_IMAGE_SIZE){
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
    public CommunityPostLikeResponse toggleLike(Long postId, User user){
        int maxRetry = 3;
        int retryCount = 0;

        while(retryCount < maxRetry){
            try {
                CommunityPost post = communityPostRepository.findById(postId)
                        .orElseThrow(PostNotFoundException::new);

                Optional<CommunityPostLike> existingLike = communityPostLikeRepository
                        .findByUserIdAndCommunityPostId(user.getId(), postId);

                if(existingLike.isPresent()){
                    return unlikePost(existingLike, post);
                }
                else{
                    return likePost(user, post);
                }
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e){
                if(++retryCount >= maxRetry){
                    throw new OptimisticLockCustomException();
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored){
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new OptimisticLockCustomException();
    }

    @Transactional(readOnly = true)
    public CommunityPostLikeResponse getLikeStatus(Long postId, User user){

        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        boolean isLiked = communityPostLikeRepository.existsByUserIdAndCommunityPostId(user.getId(), postId);

        return buildLikeResponse(isLiked, post);
    }

    private CommunityPostLikeResponse unlikePost(Optional<CommunityPostLike> existingLike, CommunityPost post){
        communityPostLikeRepository.delete(existingLike.get());
        post.decrementLikeCount();

        return buildLikeResponse(false, post);
    }

    private CommunityPostLikeResponse likePost(User user, CommunityPost post){
        CommunityPostLike like = CommunityPostLike.builder()
                .user(user)
                .communityPost(post)
                .build();

        communityPostLikeRepository.save(like);
        post.incrementLikeCount();

        return buildLikeResponse(true, post);
    }

    private CommunityPostLikeResponse buildLikeResponse(boolean isLiked, CommunityPost post){
        return CommunityPostLikeResponse.builder()
                .isLiked(isLiked)
                .likeCount(post.getLikeCount())
                .build();
    }
}
