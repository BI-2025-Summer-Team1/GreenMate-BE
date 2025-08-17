package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query("""
    SELECT p FROM CommunityPost p
    JOIN FETCH p.user
    WHERE (:lastPostId IS NULL OR p.id < :lastPostId)
    ORDER BY p.id DESC
    LIMIT :size
    """)
    List<CommunityPost> findNextPosts(@Param("lastPostId") Long lastPostId, @Param("size") int size);
}
