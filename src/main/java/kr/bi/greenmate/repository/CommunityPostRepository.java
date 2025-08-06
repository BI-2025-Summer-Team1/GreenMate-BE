package kr.bi.greenmate.repository;

import kr.bi.greenmate.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>  {
}
