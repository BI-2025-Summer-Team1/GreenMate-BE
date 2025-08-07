package kr.bi.greenmate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    @Query(value = "SELECT rp FROM RecruitmentPost rp JOIN FETCH rp.user",
           countQuery = "SELECT count(rp) FROM RecruitmentPost rp")
    Page<RecruitmentPost> findAllWithUser(Pageable pageable);
}
