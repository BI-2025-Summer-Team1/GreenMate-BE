package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.RecruitmentPostLike;

public interface RecruitmentPostLikeRepository extends JpaRepository<RecruitmentPostLike, Long> {

    boolean existsByUser_IdAndRecruitmentPost_Id(Long userId, Long recruitmentPostId);
    
    Optional<RecruitmentPostLike> findByUser_IdAndRecruitmentPost_Id(Long userId, Long recruitmentPostId);

    @Modifying
    @Query("DELETE FROM RecruitmentPostLike l WHERE l.user.id = :userId AND l.recruitmentPost.id = :recruitmentPostId")
    void deleteByUserAndRecruitmentPost(@Param("userId") Long userId, @Param("recruitmentPostId") Long recruitmentPostId);
    
    long countByRecruitmentPost_Id(Long recruitmentPostId);
}
