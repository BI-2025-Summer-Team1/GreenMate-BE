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
import kr.bi.greenmate.exception.error.CommentNotFoundException;
import kr.bi.greenmate.exception.error.FileUploadFailException;
import kr.bi.greenmate.exception.error.ParentCommentMismatchException;
import kr.bi.greenmate.exception.error.RecruitmentPostNotFoundException;
import kr.bi.greenmate.exception.error.UserNotFoundException;
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
                .orElseThrow(() -> new RecruitmentPostNotFoundException(recruitmentPostId));

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<Long> parentCommentIdOptional = Optional.ofNullable(request.getParentCommentId());

        RecruitmentPostComment parentComment = null;
        if (request.getParentCommentId() != null) {
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
