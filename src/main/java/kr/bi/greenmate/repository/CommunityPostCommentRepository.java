package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

	Optional<CommunityPostComment> findByIdAndParentId(Long commentId, Long postId);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdOrderByIdDesc(Long postId, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdAndIdLessThanOrderByIdDesc(Long postId, Long lastId, Pageable pageable);

	int deleteByIdAndParent_IdAndUser_Id(Long commentId, Long postId, Long userId);
}
