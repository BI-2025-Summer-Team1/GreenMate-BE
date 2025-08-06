package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>  {
    @Query("SELECT p FROM CommunityPost p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :postId")
    Optional<CommunityPost> findByIdWithUserAndImages(@Param("postId") Long postId);
}
