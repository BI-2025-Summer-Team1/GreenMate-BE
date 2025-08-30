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
	@EntityGraph(attributePaths = {"user", "images"})
	Optional<CommunityPost> findById(Long id);

	@EntityGraph(attributePaths = {"user"})
	Slice<CommunityPost> findAllByOrderByIdDesc(Pageable pageable);

	@EntityGraph(attributePaths = {"user"})
	Slice<CommunityPost> findByIdLessThanOrderByIdDesc(Long lastPostId, Pageable pageable);

	@Modifying
	@Query("update CommunityPost p set p.viewCount = p.viewCount + :delta where p.id = :id")
	void incrementViewCountBy(@Param("id") long id, @Param("delta") long delta);

	@Modifying
	@Query("UPDATE CommunityPost p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId AND p.commentCount > 0")
	void decrementCommentCount(@Param("postId") Long postId);
}
