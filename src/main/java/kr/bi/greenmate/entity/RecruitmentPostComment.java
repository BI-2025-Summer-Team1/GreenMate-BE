package kr.bi.greenmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RecruitmentPostComment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id", updatable = false)
	private RecruitmentPostComment parentComment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false, updatable = false)
	private RecruitmentPost recruitmentPost;

	@Column(nullable = false, length = 300)
	private String content;

	@Column(length = 50)
	private String imageUrl;
  
  @Builder
    public RecruitmentPostComment(RecruitmentPostComment parentComment, User user, RecruitmentPost recruitmentPost, String content, String imageUrl) {
        this.parentComment = parentComment;
        this.user = user;
        this.recruitmentPost = recruitmentPost;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
