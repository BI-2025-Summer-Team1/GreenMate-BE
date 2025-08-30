package kr.bi.greenmate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountPersister {

	private final CommunityPostRepository communityPostRepository;

	@Transactional
	public void persistOne(long postId, long delta) {
		communityPostRepository.incrementViewCountBy(postId, delta);
	}
}
