package com.api.farmingsoon.domain.like.model;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;


@Table(name = "likes")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeableItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Builder
    public LikeableItem(Member member, Item item) {
        this.member = member;
        this.item = item;
    }

    public static LikeableItem of(Member member, Item item) {
        return LikeableItem.builder()
                .member(member)
                .item(item)
                .build();
    }

}
