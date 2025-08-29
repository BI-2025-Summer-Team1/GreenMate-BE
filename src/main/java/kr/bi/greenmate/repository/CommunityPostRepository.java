package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	@Modifying
	@Query("UPDATE CommunityPost p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId AND p.commentCount > 0")
	int decrementCommentCount(@Param("postId") Long postId);
}
