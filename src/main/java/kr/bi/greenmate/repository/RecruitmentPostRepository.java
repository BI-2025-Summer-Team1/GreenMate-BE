package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    @Query("SELECT r FROM RecruitmentPost r JOIN FETCH r.user WHERE r.id = :id")
    Optional<RecruitmentPost> findByIdWithUser(@Param("id") Long id);
}
