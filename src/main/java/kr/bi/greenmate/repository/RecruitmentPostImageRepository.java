package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPostImage;

@Repository
public interface RecruitmentPostImageRepository extends JpaRepository<RecruitmentPostImage, Long> {

    List<RecruitmentPostImage> findByRecruitmentPostId(Long postId);
}
