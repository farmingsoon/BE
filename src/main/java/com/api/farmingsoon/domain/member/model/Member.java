package com.api.farmingsoon.domain.member.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private MemberRole role;

    @Builder
    private Member(String email, MemberRole role) {
        this.email = email;
        this.role = role;
    }
}
