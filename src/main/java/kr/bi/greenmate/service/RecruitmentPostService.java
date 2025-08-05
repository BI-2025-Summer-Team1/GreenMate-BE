package kr.bi.greenmate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.dto.RecruitmentPostCreationRequest;
import kr.bi.greenmate.dto.RecruitmentPostCreationResponse;
import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.entity.RecruitmentPostImage;
import kr.bi.greenmate.entity.User;
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
        RecruitmentPostCreationRequest request, List<String> imageUrls, Long userId) {

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RecruitmentPost post = RecruitmentPost.builder()
            .user(creator)
            .title(request.getTitle())
            .content(request.getContent())
            .activityDate(request.getActivityDate())
            .recruitmentEndDate(request.getRecruitmentEndDate())
            .build();

        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<RecruitmentPostImage> images = imageUrls.stream()
                .map(url -> RecruitmentPostImage.builder()
                    .imageUrl(url)
                    .recruitmentPost(post)
                    .build())
                .collect(Collectors.toList());
            post.getImages().addAll(images);
        }

        RecruitmentPost savedPost = recruitmentPostRepository.save(post);
        
        return RecruitmentPostCreationResponse.builder()
            .postId(savedPost.getId())
            .title(savedPost.getTitle())
            .content(savedPost.getContent())
            .authorNickname(creator.getNickname()) 
            .createdAt(savedPost.getCreatedAt())
            .imageUrls(imageUrls) 
            .build();
    }
}
