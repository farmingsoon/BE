package com.api.farmingsoon.domain.member.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private MemberRole role;

    private String profileImg;

    @Builder
    private Member(String email, MemberRole role, String profileImg, String nickname) {
        this.email = email;
        this.role = role;
        this.profileImg = profileImg;
        this.nickname = nickname;
    }

    public void updateSocialMember(String email, String nickname, String picture) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = picture;
    }
}
