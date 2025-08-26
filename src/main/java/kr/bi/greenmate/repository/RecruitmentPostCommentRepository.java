package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPostComment;

@Repository
public interface RecruitmentPostCommentRepository extends JpaRepository<RecruitmentPostComment, Long> {
    
    @EntityGraph(attributePaths = "user")
    Slice<RecruitmentPostComment> findByRecruitmentPost_IdAndParentCommentIsNullOrderByIdDesc(
            Long recruitmentPostId, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Slice<RecruitmentPostComment> findByRecruitmentPost_IdAndParentCommentIsNullAndIdLessThanOrderByIdDesc(
            Long recruitmentPostId, Long lastId, Pageable pageable);

    List<RecruitmentPostComment> findByParentCommentIdIn(List<Long> parentIds);
}
