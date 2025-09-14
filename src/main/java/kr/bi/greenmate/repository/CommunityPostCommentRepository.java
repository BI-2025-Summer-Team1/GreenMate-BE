package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

	Optional<CommunityPostComment> findByIdAndParentId(Long commentId, Long postId);

	Optional<CommunityPostComment> findByIdAndParentIdAndDeletedFalse(Long commentId, Long postId);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdOrderByIdDesc(Long postId, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdAndIdLessThanOrderByIdDesc(Long postId, Long lastId, Pageable pageable);

	@Query("select c.imageUrl from CommunityPostComment c where c.parent.id = :postId and c.imageUrl is not null")
	List<String> findImageUrlsByPostId(@Param("postId") Long postId);

	@Query("select c.imageUrl from CommunityPostComment c where c.user.id = :userId and c.imageUrl is not null")
	List<String> findImageUrlsByUserId(@Param("userId") Long userId);

	@Query(value = "update /*+ NO_PARALLEL(c) */ community_post_comment c set c.deleted = 1, c.deleted_at = SYSTIMESTAMP, c.content = '삭제된 댓글입니다.', c.image_url = null where c.user_id = :userId and c.deleted = 0", nativeQuery = true)
	void softDeleteByUserId(@Param("userId") Long userId);
	
	void deleteByParent_IdAndCommunityPostCommentIsNotNull(Long postId);

	void deleteByParent_IdAndCommunityPostCommentIsNull(Long postId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<CommunityPostComment> findById(Long commentId);
}
