package kr.bi.greenmate.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.bi.greenmate.dto.RecruitmentPostCommentRequest;
import kr.bi.greenmate.dto.RecruitmentPostCommentResponse;
import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.entity.RecruitmentPostComment;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.repository.RecruitmentPostCommentRepository;
import kr.bi.greenmate.repository.RecruitmentPostRepository;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentPostCommentService {

    private final RecruitmentPostCommentRepository recruitmentPostCommentRepository;
    private final UserRepository userRepository;
    private final RecruitmentPostRepository recruitmentPostRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public RecruitmentPostCommentResponse createComment(
            Long recruitmentPostId, Long userId, RecruitmentPostCommentRequest request, MultipartFile image) {

        RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(recruitmentPostId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집글입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Optional<Long> parentCommentIdOptional = Optional.ofNullable(request.getParentCommentId());

        RecruitmentPostComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = recruitmentPostCommentRepository.findById(parentCommentIdOptional.get())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));

            if (!parentComment.getRecruitmentPost().getId().equals(recruitmentPostId)) {
                throw new IllegalArgumentException("부모 댓글이 해당 모집글에 속하지 않습니다.");
            }
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageUploadService.upload(image, "recruitment-comment");
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
        recruitmentPostRepository.save(recruitmentPost);

        return RecruitmentPostCommentResponse.builder()
                .id(recruitmentPostComment.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .content(recruitmentPostComment.getContent())
                .createdAt(recruitmentPostComment.getCreatedAt())
                .build();
    }
}
