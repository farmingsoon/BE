package com.api.farmingsoon.domain.item.domain;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE item SET deleted_at = true WHERE id = ?")
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
    private Long hopePrice;

    @Column
    private LocalDateTime expiredAt;

    @Column
    @Setter
    private String thumbnailImageUrl;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @OneToMany(mappedBy = "item")
    private List<Bid> bidList;

    public void updateItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    @Builder
    private Item(Member member, String title, String description, Long hopePrice, LocalDateTime expiredAt, String thumbnailImageUrl, boolean deleted, ItemStatus itemStatus) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.expiredAt = expiredAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.itemStatus = itemStatus;
    }


}
