package kr.bi.greenmate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.bi.greenmate.dto.RecruitmentPostCreationRequest;
import kr.bi.greenmate.dto.RecruitmentPostCreationResponse;
import kr.bi.greenmate.dto.RecruitmentPostDetailResponse;
import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.entity.RecruitmentPostImage;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.RecruitmentPostNotFoundException;
import kr.bi.greenmate.exception.error.UserNotFoundException;
import kr.bi.greenmate.repository.RecruitmentPostRepository;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruitmentPostService {

    private final RecruitmentPostRepository recruitmentPostRepository;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService; 

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

    @Transactional(readOnly = true)
    public RecruitmentPostDetailResponse getPostDetail(Long postId) {
        RecruitmentPost post = recruitmentPostRepository.findByIdWithUser(postId)
            .orElseThrow(() -> new RecruitmentPostNotFoundException(postId));
            
        List<String> imageUrls = recruitmentPostRepository.findImagesByPostId(postId).stream()
            .map(RecruitmentPostImage::getImageUrl)
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
}
