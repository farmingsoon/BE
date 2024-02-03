package com.api.farmingsoon.domain.like.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{itemId}")
    public Response<Void> like(@PathVariable(name = "itemId") Long itemId) {
        likeService.like(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품 좋아요 등록", itemId));
    }

    @DeleteMapping("/{itemId}")
    public Response<Void> delete(@PathVariable(name = "itemId") Long itemId) {
        likeService.delete(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품의 좋아요 취소", itemId));
    }

    @GetMapping("/me")
    public Response<ItemListResponse> likedItemList(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(HttpStatus.OK, "좋아요를 누른 상품의 목록 조회 성공", likeService.likedItemList(pageable));
    }

    @GetMapping("/{itemId}")
    public Response<Long> likeCount(@PathVariable(name = "itemId") Long itemId) {
        return Response.success(HttpStatus.OK, String.format("%d번 상품의 좋아요 개수 조회", itemId), likeService.likeCount(itemId));
    }

}
