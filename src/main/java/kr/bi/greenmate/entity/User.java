package kr.bi.greenmate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List; 

@Entity
@Table(name = "USERS") 
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class User extends BaseTimeEntity { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, precision = 38, scale = 0) 
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100) 
    private String email;

    @Column(name = "NICKNAME", nullable = false, unique = true, length = 10) 
    private String nickname;

    @Column(name = "PASSWORD", nullable = false, length = 60) 
    private String password;

    @Column(name = "PROFILE_IMAGE_URL", length = 50) 
    private String profileImageUrl; 

    @Column(name = "SELF_INTRODUCTION", nullable = false, length = 300) 
    private String selfIntroduction; 


    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommunityPost> communityPosts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommunityPostComment> communityPostComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommunityPostLike> communityPostLikes = new ArrayList<>();

}