package com.api.farmingsoon.domain.item.domain;

import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column
    private Long hopePrice;

    @Column
    private LocalDateTime expireAt;

    @Column
    @Setter
    private String thumbnailImageUrl;

    @ColumnDefault("false")
    private boolean deleted;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @Builder
    private Item(String title, String description, Long hopePrice, LocalDateTime expireAt, String thumbnailImageUrl, boolean deleted, ItemStatus itemStatus) {
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.expireAt = expireAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.deleted = deleted;
        this.itemStatus = itemStatus;
    }
}
