package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

    Optional<CommunityPostLike> findByUserIdAndCommunityPostId(Long userId, Long postId);

    boolean existsByUserIdAndCommunityPostId(Long userId, Long postId);

    @Query("""
    SELECT l.communityPost.id
    FROM CommunityPostLike l
    WHERE l.user.id = :userId AND l.communityPost.id IN :postIds
""")
    List<Long> findLikedPostIdsByUserIdAndPostIds(Long userId, List<Long> postIds);
}
