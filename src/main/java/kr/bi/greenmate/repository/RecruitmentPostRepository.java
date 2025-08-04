package kr.bi.greenmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {
}
