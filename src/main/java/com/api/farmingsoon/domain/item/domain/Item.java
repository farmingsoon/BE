package com.api.farmingsoon.domain.item.domain;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE item SET deleted_at = true WHERE id = ?")
@SQLRestriction("deleted_at IS NOT NULL")
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
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

    @Builder
    private Item(String title, String description, Long hopePrice, LocalDateTime expiredAt, String thumbnailImageUrl, boolean deleted, ItemStatus itemStatus) {
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.expiredAt = expiredAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.itemStatus = itemStatus;
    }
}