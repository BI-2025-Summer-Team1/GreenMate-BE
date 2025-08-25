package kr.bi.greenmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {
	
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
		"FROM CommunityPostComment c " +
		"WHERE c.id = :commentId AND c.parent.id = :postId")
	boolean existsByCommentIdAndPostId(@Param("commentId") Long commentId, @Param("postId") Long postId);
}
