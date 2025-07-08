package kr.bi.greenmate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_post_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost communityPost;

    @Column(name = "image_url", length = 50, nullable = false)
    private String imageUrl;
}
