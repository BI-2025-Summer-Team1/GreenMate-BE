package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPostComment;

@Repository
public interface RecruitmentPostCommentRepository extends JpaRepository<RecruitmentPostComment, Long> {
    
    Slice<RecruitmentPostComment> findByRecruitmentPostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    List<RecruitmentPostComment> findByParentCommentIdIn(List<Long> parentIds);
}
