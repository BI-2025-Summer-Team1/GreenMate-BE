package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.CommunityPostImage;

public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {

	@Query("SELECT cpi.imageUrl FROM CommunityPostImage cpi WHERE cpi.communityPost.id = :postId")
	List<String> findImageUrlsByPostId(@Param("postId") Long postId);

	@Query("""
		    select i.imageUrl
		    from CommunityPostImage i
		    where i.communityPost.id in (
		        select p.id from CommunityPost p where p.user.id = :userId
		    )
		""")
	List<String> findImageKeysByOwner(Long userId);
}
