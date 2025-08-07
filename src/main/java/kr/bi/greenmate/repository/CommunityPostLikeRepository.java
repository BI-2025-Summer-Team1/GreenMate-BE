package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

    @Query("SELECT cpl FROM CommunityPostLike cpl WHERE cpl.user.id = :userId AND cpl.communityPost.id = :postId")
    Optional<CommunityPostLike> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT COUNT(cpl) > 0 FROM CommunityPostLike cpl WHERE cpl.user.id = :userId AND cpl.communityPost.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
