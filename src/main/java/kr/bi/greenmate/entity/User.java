package kr.bi.greenmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; 

@Entity
@Table(name = "users") 
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, precision = 38, scale = 0) 
    private Long id;

    @Column(nullable = false, unique = true, length = 100) 
    private String email;

    @Column(nullable = false, unique = true, length = 10) 
    private String nickname;

    @Column(nullable = false, length = 60) 
    private String password;

    @Column(length = 50) 
    private String profileImageUrl; 

    @Column(nullable = false, length = 300) 
    private String selfIntroduction; 

}
