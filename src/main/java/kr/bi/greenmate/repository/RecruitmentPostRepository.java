package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<RecruitmentPost> findByIdWithUser(Long postId);
}
