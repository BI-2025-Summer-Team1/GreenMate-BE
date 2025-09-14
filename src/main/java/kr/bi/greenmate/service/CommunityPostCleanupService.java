package kr.bi.greenmate.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.repository.CommunityPostCommentRepository;
import kr.bi.greenmate.repository.CommunityPostImageRepository;
import kr.bi.greenmate.repository.CommunityPostRepository;
import kr.bi.greenmate.repository.CommunityPostLikeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityPostCleanupService {
	private final CommunityPostRepository communityPostRepository;
	private final CommunityPostCommentRepository communityPostCommentRepository;
	private final CommunityPostImageRepository communityPostImageRepository;
	private final CommunityPostLikeRepository communityPostLikeRepository;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public void deleteAllOfUser(Long userId) {
		List<String> imageKeys = new ArrayList<>();
		// 1) 사용자가 작성한 커뮤니티 글을 계층적으로 하드 삭제
		List<Long> postIds = communityPostRepository.findIdsByUserId(userId);
		for (Long postId : postIds) {
			List<String> postImageKeys = communityPostImageRepository.findImageUrlsByPostId(postId);
			List<String> commentImageKeys = communityPostCommentRepository.findImageUrlsByPostId(postId);
			imageKeys.addAll(postImageKeys);
			imageKeys.addAll(commentImageKeys);

			communityPostCommentRepository.deleteByParent_IdAndCommunityPostCommentIsNotNull(postId);
			communityPostCommentRepository.deleteByParent_IdAndCommunityPostCommentIsNull(postId);
			communityPostLikeRepository.deleteByCommunityPostId(postId);
			communityPostImageRepository.deleteByCommunityPostId(postId);
			communityPostRepository.deleteById(postId);
		}

		// 2) 타인의 글에 남긴 사용자의 댓글은 soft delete
		imageKeys.addAll(communityPostCommentRepository.findImageUrlsByUserId(userId));
		communityPostCommentRepository.softDeleteByUserId(userId);

		if (!imageKeys.isEmpty()) {
			publisher.publishEvent(new ImagesToDeleteEvent(imageKeys));
		}
	}
}
