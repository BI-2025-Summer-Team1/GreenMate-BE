package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPostComment;

@Repository
public interface RecruitmentPostCommentRepository extends JpaRepository<RecruitmentPostComment, Long> {
    
    @Query("SELECT c FROM RecruitmentPostComment c JOIN FETCH c.user u WHERE c.recruitmentPost.id = :recruitmentPostId AND c.parentComment IS NULL")
    Page<RecruitmentPostComment> findByRecruitmentPostIdAndParentCommentIsNull(@Param("recruitmentPostId") Long recruitmentPostId, Pageable pageable);

    @Query("SELECT c FROM RecruitmentPostComment c JOIN FETCH c.user WHERE c.parentComment.id IN :parentIds")
    List<RecruitmentPostComment> findByParentCommentIdIn(@Param("parentIds") List<Long> parentIds);
}
