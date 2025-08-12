package kr.bi.greenmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.entity.RecruitmentPostImage;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    @Query("SELECT rp FROM RecruitmentPost rp JOIN FETCH rp.user WHERE rp.id = :postId")
    Optional<RecruitmentPost> findByIdWithUser(@Param("postId") Long postId);

    @Query("SELECT rpi FROM RecruitmentPostImage rpi WHERE rpi.recruitmentPost.id = :postId")
    List<RecruitmentPostImage> findImagesByPostId(@Param("postId") Long postId);
}
