package kr.bi.greenmate.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.repository.RecruitmentPostCommentRepository;
import kr.bi.greenmate.repository.RecruitmentPostImageRepository;
import kr.bi.greenmate.repository.RecruitmentPostLikeRepository;
import kr.bi.greenmate.repository.RecruitmentPostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentPostCleanupService {

	private final RecruitmentPostRepository recruitmentPostRepository;
	private final RecruitmentPostCommentRepository recruitmentPostCommentRepository;
	private final RecruitmentPostImageRepository recruitmentPostImageRepository;
	private final RecruitmentPostLikeRepository recruitmentPostLikeRepository;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public void deleteAllOfUser(Long userId) {
		List<String> imageKeys = new ArrayList<>();

		// 1) 사용자가 작성한 모집글을 계층적으로 하드 삭제
		List<Long> postIds = recruitmentPostRepository.findIdsByUserId(userId);
		for (Long postId : postIds) {
			List<String> postImageKeys = recruitmentPostImageRepository.findImageUrlsByPostId(postId);
			imageKeys.addAll(postImageKeys);

			recruitmentPostCommentRepository.deleteByRecruitmentPost_IdAndParentCommentIsNotNull(postId);
			recruitmentPostCommentRepository.deleteByRecruitmentPost_IdAndParentCommentIsNull(postId);
			recruitmentPostLikeRepository.deleteByRecruitmentPostId(postId);
			// 이미지 엔티티 삭제 + 게시글 삭제
			recruitmentPostImageRepository.findByRecruitmentPostId(postId).forEach(img -> {});
			recruitmentPostImageRepository.deleteAll(recruitmentPostImageRepository.findByRecruitmentPostId(postId));
			recruitmentPostRepository.deleteById(postId);
		}

		// 2) 타인의 모집글에 남긴 사용자의 댓글 soft delete
		recruitmentPostCommentRepository.softDeleteByUserId(userId);

		if (!imageKeys.isEmpty()) {
			publisher.publishEvent(new ImagesToDeleteEvent(imageKeys));
		}
	}
}


