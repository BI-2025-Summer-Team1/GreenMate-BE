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

		List<Long> postIds = recruitmentPostRepository.findIdsByUserId(userId);
		for (Long postId : postIds) {
			List<String> postImageKeys = recruitmentPostImageRepository.findImageUrlsByPostId(postId);
			imageKeys.addAll(postImageKeys);

			recruitmentPostCommentRepository.deleteByRecruitmentPost_IdAndParentCommentIsNotNull(postId);
			recruitmentPostCommentRepository.deleteByRecruitmentPost_IdAndParentCommentIsNull(postId);
			recruitmentPostLikeRepository.deleteByRecruitmentPostId(postId);

			recruitmentPostImageRepository.findByRecruitmentPostId(postId).forEach(img -> {
			});
			recruitmentPostImageRepository.deleteAll(recruitmentPostImageRepository.findByRecruitmentPostId(postId));
			recruitmentPostRepository.deleteById(postId);
		}

		recruitmentPostCommentRepository.softDeleteByUserId(userId);

		if (!imageKeys.isEmpty()) {
			publisher.publishEvent(new ImagesToDeleteEvent(imageKeys));
		}
	}
}


