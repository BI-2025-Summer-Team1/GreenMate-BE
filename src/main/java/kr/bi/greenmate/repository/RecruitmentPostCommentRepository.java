package kr.bi.greenmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import kr.bi.greenmate.entity.RecruitmentPostComment;

@Repository
public interface RecruitmentPostCommentRepository extends JpaRepository<RecruitmentPostComment, Long> {

    @Transactional
    void deleteByRecruitmentPostId(Long postId);
}
