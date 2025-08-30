package kr.bi.greenmate.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.repository.CommunityPostCommentRepository;
import kr.bi.greenmate.repository.CommunityPostImageRepository;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityPostCleanupService {
	private final CommunityPostRepository communityPostRepository;
	private final CommunityPostCommentRepository communityPostCommentRepository;
	private final CommunityPostImageRepository communityPostImageRepository;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public void deleteAllOfUser(Long userId) {
		List<String> imageKeys = communityPostImageRepository.findImageKeysByOwner(userId);

		communityPostCommentRepository.deleteByUser_Id(userId);

		communityPostRepository.deleteByUser_Id(userId);

		if (!imageKeys.isEmpty()) {
			publisher.publishEvent(new ImagesToDeleteEvent(imageKeys));
		}
	}
}
