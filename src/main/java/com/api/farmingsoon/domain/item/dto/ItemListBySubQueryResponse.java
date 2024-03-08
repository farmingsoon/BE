package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemListBySubQueryResponse {

    private Pagination<ItemBySubQueryResponse> pagination; // 페이지 관련 데이터
    private List<ItemBySubQueryResponse> itemBySubQueryResponseList; // 페이지 관련 데이터

    public ItemListBySubQueryResponse(Pagination<ItemBySubQueryResponse> pagination, List<ItemBySubQueryResponse> itemBySubQueryResponseList) {
        this.pagination = pagination;
        this.itemBySubQueryResponseList = itemBySubQueryResponseList;
    }
}
