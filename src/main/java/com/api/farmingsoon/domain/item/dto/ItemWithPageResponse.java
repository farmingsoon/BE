package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ItemWithPageResponse {

    private List<ItemDto> items; // 상품 데이터
    private Pagination pagination; // 페이지 관련 데이터

    public static ItemWithPageResponse of(Page<Item> itemPage) {
        Page<ItemDto> itemDtoPage = itemPage.map(ItemDto::of); // Page<Item> -> Page<ItemDto>
        return ItemWithPageResponse.builder()
                .items(itemDtoPage.getContent())
                .pagination(Pagination.of(itemDtoPage))
                .build();
    }

    @Getter
    @Builder
    private static class ItemDto {

        private Long itemId;
        private String title;
        private Long writerId;
        private String writerNickname;
        private LocalDateTime createdAt;

        private static ItemDto of(Item item) {
            return ItemDto.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .writerId(item.getMember().getId())
                    .writerNickname(item.getMember().getNickname())
                    .createdAt(item.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    private static class Pagination {

        private int totalPages; // 전체 페이지수
        private long totalElements; // 전체 개수
        private int page; // 현재 페이지
        private boolean hasNext; // 다음 페이지 존재 여부
        private boolean hasPrevious; // 이전 페이지 존재 여부
        private int requestSize;
        private int itemSize;

        private static Pagination of(Page<ItemDto> itemDtoPage) {
            return builder()
                    .totalPages(itemDtoPage.getTotalPages())
                    .totalElements(itemDtoPage.getTotalElements())
                    .page(itemDtoPage.getNumber() + 1)
                    .hasNext(itemDtoPage.hasNext())
                    .hasPrevious(itemDtoPage.hasPrevious())
                    .requestSize(itemDtoPage.getSize())
                    .itemSize(itemDtoPage.getNumberOfElements())
                    .build();
        }
    }
}
