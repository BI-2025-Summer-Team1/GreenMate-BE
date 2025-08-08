package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {

    @Query("SELECT cpi.imageUrl FROM CommunityPostImage cpi WHERE cpi.communityPost.id = :postId")
    List<String> findImageUrlsByPostId(@Param("postId") Long postId);
}
