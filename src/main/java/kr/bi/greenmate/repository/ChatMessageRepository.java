package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId, Pageable pageable);

	List<ChatMessage> findByUserIdAndSessionIdAndIdLessThanOrderByCreatedAtDesc(Long userId, Long sessionId,
		Long cursor, Pageable pageable);

	Optional<ChatMessage> findTopByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);
}
