package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.CommunityPostComment;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

	Optional<CommunityPostComment> findByIdAndParentId(Long commentId, Long postId);
}
