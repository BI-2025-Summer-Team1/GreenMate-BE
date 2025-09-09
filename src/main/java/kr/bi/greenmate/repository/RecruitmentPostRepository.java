package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.RecruitmentPost;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

	@Query(value = "SELECT rp FROM RecruitmentPost rp JOIN FETCH rp.user", countQuery = "SELECT count(rp) FROM RecruitmentPost rp")
	Page<RecruitmentPost> findAllWithUser(Pageable pageable);

	@Query("SELECT r FROM RecruitmentPost r JOIN FETCH r.user WHERE r.id = :id")
	Optional<RecruitmentPost> findByIdWithUser(@Param("id") Long id);

	@Query("SELECT ra.recruitmentPost FROM RecruitmentApplication ra JOIN FETCH ra.recruitmentPost.user WHERE ra.user.id = :userId")
	Slice<RecruitmentPost> findParticipatedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
