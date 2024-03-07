package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemListResponseBySubQuery {

    private Pagination<ItemResponseBySubQuery> pagination; // 페이지 관련 데이터
    private List<ItemResponseBySubQuery> itemResponseBySubQueryList; // 페이지 관련 데이터

    public ItemListResponseBySubQuery(Pagination<ItemResponseBySubQuery> pagination, List<ItemResponseBySubQuery> itemResponseBySubQueryList) {
        this.pagination = pagination;
        this.itemResponseBySubQueryList = itemResponseBySubQueryList;
    }
}
