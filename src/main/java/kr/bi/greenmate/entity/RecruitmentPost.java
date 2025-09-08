package kr.bi.greenmate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
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
public class RecruitmentPost extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(nullable = false, length = 100) // 기존 50에서 communityPost와 유사하게 제목 길이를 통일
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	@Builder.Default
	@Column(nullable = false)
	private Long participantCount = 0L;

	@Builder.Default
	@Column(nullable = false)
	private Long viewCount = 0L;

	@Builder.Default
	@Column(nullable = false)
	private Long likeCount = 0L;

	@Builder.Default
	@Column(nullable = false)
	private Long commentCount = 0L;

	@Column(nullable = false)
	private LocalDateTime activityDate;

	@Column(nullable = false)
	private LocalDateTime recruitmentEndDate;

	@Version
	@Column(nullable = false)
	private Long version;

	@OneToMany(mappedBy = "recruitmentPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	private List<RecruitmentPostImage> images = new ArrayList<>();

	public void increaseLikeCount() {
		if (this.likeCount == null) {
			this.likeCount = 0L;
		}
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (this.likeCount == null || this.likeCount <= 0) {
			this.likeCount = 0L;
		} else {
			this.likeCount--;
		}
	}

	public void increaseCommentCount() {
		this.commentCount++;
	}
  
  public void decreaseCommentCount() {
    if (this.commentCount > 0) {  
        this.commentCount--;  
    }
  }

  public void incrementViewCountBy(long delta) {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        this.viewCount += delta;
    }
}
