package com.api.farmingsoon.common.pagenation;

import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class Pagination<T> {

    private int totalPageSize; // 전체 페이지수
    private long totalElementSize; // 전체 개수
    private int page; // 현재 페이지(1부터 시작)
    private boolean hasNext; // 다음 페이지 존재 여부
    private boolean hasPrevious; // 이전 페이지 존재 여부
    private int pageSize; // 현재 페이지의 전체 사이즈
    private int elementSize; // 현재 페이지에 있는 요소의 수

    @Builder
    public Pagination(int totalPageSize, long totalElementSize, int page, boolean hasNext, boolean hasPrevious, int pageSize, int elementSize) {
        this.totalPageSize = totalPageSize;
        this.totalElementSize = totalElementSize;
        this.page = page;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.pageSize = pageSize;
        this.elementSize = elementSize;
    }

    /**
     * @Description
     * T 타입으로 원시 타입이 들어올 가능성이 없기에 경고를 제거합니다.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Pagination of(Page<T> page) {
        return (Pagination<T>) Pagination.builder()
                .totalPageSize(page.getTotalPages())
                .totalElementSize(page.getTotalElements())
                .page(page.getNumber() + 1)
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .pageSize(page.getSize())
                .elementSize(page.getNumberOfElements())
                .build();
    }
}
