package kr.bi.greenmate.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPostComment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", updatable = false)
	private CommunityPostComment communityPostComment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false, updatable = false)
	private CommunityPost parent;

	@Column(length = 100, nullable = false)
	private String content;

	@Column(length = 50)
	private String imageUrl;

	@Column(nullable = false)
	@Builder.Default
	private boolean deleted = false;

	@Column
	private LocalDateTime deletedAt;

	public void markAsDeleted() {
		if (this.deleted) {
			return;
		}
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
		this.content = "삭제된 댓글입니다.";
		this.imageUrl = null;
	}
}
