package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    @Query("SELECT rp FROM RecruitmentPost rp " +
           "JOIN FETCH rp.user " +
           "LEFT JOIN FETCH rp.images " +
           "WHERE rp.id = :postId")
    Optional<RecruitmentPost> findByIdWithUserAndImages(@Param("postId") Long postId);
}
