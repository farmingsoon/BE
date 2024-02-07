package com.api.farmingsoon.domain.like.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.item.dto.LikeableItemListResponse;
import com.api.farmingsoon.domain.like.service.LikeableItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/likeable-items")
@RequiredArgsConstructor
public class LikeableItemController {

    private final LikeableItemService likeableItemService;

    @PostMapping("/{itemId}")
    public Response<Void> like(@PathVariable(name = "itemId") Long itemId) {
        likeableItemService.like(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품 좋아요 등록", itemId));
    }

    @DeleteMapping("/{itemId}")
    public Response<Long> delete(@PathVariable(name = "itemId") Long itemId) {
        likeableItemService.delete(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품의 좋아요 취소", itemId), itemId);
    }

    @GetMapping("/me")
    public Response<LikeableItemListResponse> getLikeableItemList(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(HttpStatus.OK, "좋아요를 누른 상품 목록 조회 성공", likeableItemService.likableItemList(pageable));
    }
}
