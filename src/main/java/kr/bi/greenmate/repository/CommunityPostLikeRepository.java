package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostLike;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

	Optional<CommunityPostLike> findByUserIdAndCommunityPostId(Long userId, Long postId);

	boolean existsByUserIdAndCommunityPostId(Long userId, Long postId);

	@Query("""
		    SELECT l.communityPost.id
		    FROM CommunityPostLike l
		    WHERE l.user.id = :userId AND l.communityPost IN :posts
		""")
	List<Long> findLikedPostIdsByUserIdAndPosts(
		@Param("userId") Long userId,
		@Param("posts") List<CommunityPost> posts);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<CommunityPostLike> findAllByCommunityPostId(Long postId);

	@Modifying(clearAutomatically = true)
	void deleteByCommunityPostId(Long postId);
}
