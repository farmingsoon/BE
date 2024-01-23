package com.api.farmingsoon.domain.image.domain;


import com.api.farmingsoon.domain.item.domain.Item;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Builder
    private Image(String imageUrl, Item item) {
        this.imageUrl = imageUrl;
        this.item = item;
    }

    public static Image of(String imageUrl, Item item) {
        return Image.builder()
                .imageUrl(imageUrl)
                .item(item)
                .build();
    }
}
