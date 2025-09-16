package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.ChatMessages;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {

	List<ChatMessages> findByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId, Pageable pageable);

	List<ChatMessages> findByUserIdAndSessionIdAndIdLessThanOrderByCreatedAtDesc(Long userId, Long sessionId,
		Long cursor, Pageable pageable);

	Optional<ChatMessages> findTopByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);
}
