package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.RecruitmentPostLike;

public interface RecruitmentPostLikeRepository extends JpaRepository<RecruitmentPostLike, Long> {
  
	boolean existsByUser_IdAndRecruitmentPost_Id(Long userId, Long recruitmentPostId);

	Optional<RecruitmentPostLike> findByUser_IdAndRecruitmentPost_Id(Long userId, Long recruitmentPostId);
  
  void deleteByRecruitmentPostId(Long postId);
}
