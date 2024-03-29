package com.api.farmingsoon.domain.item.domain;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.image.domain.Image;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE item SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
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

    @Setter
    @Column
    private Integer awardPrice;

    @Column
    private Integer bidPeriod;

    @Column
    private LocalDateTime expiredAt;

    @Column
    @Setter
    private String thumbnailImageUrl;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    private Integer viewCount;

    // *Todo 양방향 안쓰는 쪽으로 고려해보기
    @OneToMany(mappedBy = "item")
    @BatchSize(size = 12)
    private List<Bid> bidList;

    @BatchSize(size = 12)
    @OneToMany(mappedBy = "item")
    private List<LikeableItem> likeableItemList;

    @OneToMany(mappedBy = "item")
    private List<Image> imageList;



    @Builder
    private Item(Member member, String title, String description, Integer hopePrice, Integer awardPrice, Integer bidPeriod, LocalDateTime expiredAt, String thumbnailImageUrl, String category, ItemStatus itemStatus, Integer viewCount) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.awardPrice = awardPrice;
        this.bidPeriod = bidPeriod;
        this.category = category;
        this.expiredAt = expiredAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.itemStatus = itemStatus;
        this.viewCount = viewCount == null ? 0 : viewCount;
    }


    public void increaseViewCount(Integer viewCount) {
        this.viewCount += viewCount;
    }
    public void updateItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }
}
