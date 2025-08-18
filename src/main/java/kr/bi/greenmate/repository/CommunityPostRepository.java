package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"user"})
    @Query("""
    SELECT p FROM CommunityPost p
    ORDER BY p.id DESC
""")
    List<CommunityPost> findFirstPage(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
    SELECT p FROM CommunityPost p
    WHERE p.id < :lastPostId
    ORDER BY p.id DESC
""")
    List<CommunityPost> findNextPage(@Param("lastPostId") Long lastPostId, Pageable pageable);
}
