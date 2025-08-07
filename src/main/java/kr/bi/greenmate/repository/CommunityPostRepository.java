package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>  {
    @Query("""
    SELECT p 
    FROM CommunityPost p
    JOIN FETCH p.user
    WHERE p.id = :postId""")
    Optional<CommunityPost> findByIdWithUserAndImages(@Param("postId") Long postId);

    @Query("SELECT cpi.imageUrl FROM CommunityPostImage cpi WHERE cpi.communityPost.id = :postId")
    List<String> findImageUrlsByPostId(@Param("postId") Long postId);
}
