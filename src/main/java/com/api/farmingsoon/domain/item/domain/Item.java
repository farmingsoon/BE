package com.api.farmingsoon.domain.item.domain;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Member member;

    @Column(length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Column
    private Integer hopePrice;

    @Column
    private LocalDateTime expiredAt;

    @Column
    @Setter
    private String thumbnailImageUrl;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    private Long viewCount;

    // *Todo 양방향 안쓰는 쪽으로 고려해보기
    @OneToMany(mappedBy = "item")
    private List<Bid> bidList;

    @OneToMany(mappedBy = "item")
    private List<LikeableItem> likeableItemList;

    public void updateItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    @Builder
    private Item(Member member, String title, String description, Integer hopePrice, LocalDateTime expiredAt, String thumbnailImageUrl, String category, ItemStatus itemStatus, Long viewCount) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.category = category;
        this.expiredAt = expiredAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.itemStatus = itemStatus;
        this.viewCount = viewCount;
    }


}
