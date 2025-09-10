package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.bi.greenmate.entity.CommunityPost;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
	@Query("""
		SELECT p 
		FROM CommunityPost p
		JOIN FETCH p.user
		WHERE p.id = :postId""")
	Optional<CommunityPost> findByIdWithUserAndImages(@Param("postId") Long postId);

	@EntityGraph(attributePaths = {"user"})
	@Query("""
		SELECT p FROM CommunityPost p
		ORDER BY p.id DESC
		""")
	Slice<CommunityPost> findFirstPage(Pageable pageable);

	@EntityGraph(attributePaths = {"user"})
	@Query("""
		SELECT p FROM CommunityPost p
		WHERE p.id < :lastPostId
		ORDER BY p.id DESC
		""")
	Slice<CommunityPost> findNextPage(@Param("lastPostId") Long lastPostId, Pageable pageable);

	@Modifying
	@Query("update CommunityPost p set p.viewCount = p.viewCount + :delta where p.id = :id")
	int incrementViewCountBy(@Param("id") long id, @Param("delta") long delta);

	void deleteByUser_Id(Long userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<CommunityPost> findWithLockById(Long postId);

	@EntityGraph(attributePaths = {"user"})
	Slice<CommunityPost> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

	@EntityGraph(attributePaths = {"user"})
	Slice<CommunityPost> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long lastPostId, Pageable pageable);
}
