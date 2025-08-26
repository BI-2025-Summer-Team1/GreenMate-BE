package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {
	
	boolean existsByCommentIdAndPostId(@Param("commentId") Long commentId, @Param("postId") Long postId);

	Optional<CommunityPostComment> findByIdAndPostId(@Param("commentId") Long commentId, @Param("postId") Long postId);
}
