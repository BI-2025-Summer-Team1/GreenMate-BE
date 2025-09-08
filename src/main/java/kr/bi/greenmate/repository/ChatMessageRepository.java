package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query("SELECT cm FROM ChatMessage cm WHERE cm.userId = :userId AND cm.sessionId = :sessionId ORDER BY cm.createdAt DESC")
	List<ChatMessage> findByUserIdAndSessionIdOrderByCreatedAtDesc(
		@Param("userId") Long userId,
		@Param("sessionId") Long sessionId,
		Pageable pageable
	);

	@Query("SELECT cm FROM ChatMessage cm WHERE cm.userId = :userId AND cm.sessionId = :sessionId AND cm.id < :cursor ORDER BY cm.createdAt DESC")
	List<ChatMessage> findByUserIdAndSessionIdAndIdLessThanOrderByCreatedAtDesc(
		@Param("userId") Long userId,
		@Param("sessionId") Long sessionId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	Optional<ChatMessage> findTopByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);
}
