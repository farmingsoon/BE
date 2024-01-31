package com.api.farmingsoon.domain.bid.model;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Bid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int price; // 입찰 가격

    @Enumerated(EnumType.STRING)
    private BidResult bidResult; // 입찰 결과?

    public static Bid of(Item item, Member member, int price, BidResult bidResult) {
        return Bid.builder()
                .item(item)
                .member(member)
                .price(price)
                .bidResult(bidResult)
                .build();
    }
}
