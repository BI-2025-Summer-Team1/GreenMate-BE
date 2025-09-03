package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

	Optional<CommunityPostComment> findByIdAndParentId(Long commentId, Long postId);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdOrderByIdDesc(Long postId, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	List<CommunityPostComment> findByParent_IdAndIdLessThanOrderByIdDesc(Long postId, Long lastId, Pageable pageable);

	void deleteByUser_Id(Long userId);

	@Query("select c.imageUrl from CommunityPostComment c where c.parent.id = :postId and c.imageUrl is not null")
	List<String> findImageUrlsByPostId(Long postId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<CommunityPostComment> findAllByParentId(Long postId);

	@Modifying(clearAutomatically = true)
	void deleteByParentId(Long postId);
}
