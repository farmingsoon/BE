package com.api.farmingsoon.domain.member.model;


import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Setter
    private String profileImg;

    @Builder
    private Member(String email, MemberRole role, String profileImg, String nickname, String password) {
        this.email = email;
        this.role = role;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.password = password;
    }

    public void updateSocialMember(String email, String nickname, String picture) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = picture;
    }

    public void setEncryptedPassword(String password){
        this.password = password;
    }

}
