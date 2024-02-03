package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemDetailResponse {

    private String title;
    private String description;
    private long hopePrice;
    private LocalDateTime expiredAt;
    private ItemStatus itemStatus;

    public static ItemDetailResponse fromEntity(Item item) {
        return ItemDetailResponse.builder()
                .title(item.getTitle())
                .description(item.getDescription())
                .hopePrice(item.getHopePrice())
                .expiredAt(item.getExpiredAt())
                .itemStatus(item.getItemStatus())
                .build();
    }

}
