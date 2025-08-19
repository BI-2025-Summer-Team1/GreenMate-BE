package kr.bi.greenmate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public RecruitmentPostCommentResponse createComment(Long recruitmentPostId, Long userId, RecruitmentPostCommentRequest request) {
        RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(recruitmentPostId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집글입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        RecruitmentPostComment recruitmentPostComment = RecruitmentPostComment.builder()
                .recruitmentPost(recruitmentPost)
                .user(user)
                .content(request.getContent())
                .build();

        recruitmentPostCommentRepository.save(recruitmentPostComment);

        return RecruitmentPostCommentResponse.builder()
                .id(recruitmentPostComment.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .content(recruitmentPostComment.getContent())
                .createdAt(recruitmentPostComment.getCreatedAt())
                .build();
    }
}
