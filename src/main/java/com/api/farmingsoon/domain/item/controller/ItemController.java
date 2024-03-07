package com.api.farmingsoon.domain.item.controller;


import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.common.util.CookieUtils;
import com.api.farmingsoon.domain.item.dto.*;
import com.api.farmingsoon.domain.item.service.ItemService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @LoginChecking
    @PostMapping
    public Response<Long> createItem(@ModelAttribute @Valid ItemCreateRequest itemCreateRequest) {
        Long itemId = itemService.createItem(itemCreateRequest);
        return Response.success(HttpStatus.OK, "상품 등록이 완료되었습니다.", itemId);
    }
    @GetMapping("/{itemId}")
    public Response<ItemDetailResponse> getItemDetail(@Valid @PathVariable(value = "itemId") Long itemId, HttpServletRequest request, HttpServletResponse response) {
        ItemDetailResponse itemDetailResponse = itemService.getItemDetail(itemId);

        /**
         * @Description
         * 1. 쿠키가 없다면 만들고 있다면 value Return
         * 2. 사용자의 아이템에 대한 접근 흔적이 없다면 조회수 증가
         */
        String viewCountCookieValue = CookieUtils.getViewCountCookieValue(request, response);
        itemService.handleViewCount(itemId, viewCountCookieValue);


        return Response.success(HttpStatus.OK, String.format("%d번 상품 정보입니다.",itemId), itemDetailResponse);
    }

    @GetMapping
    public Response<ItemListResponse> getItemList(
            @PageableDefault(size = 12) Pageable pageable,
            @RequestParam(value = "sortcode", defaultValue = "recent") String sortcode,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {

        ItemListResponse items = itemService.getItemList(category, keyword, pageable, sortcode);
        return Response.success(HttpStatus.OK, "상품 목록 조회 성공!", items);
    }
    @GetMapping("/test")
    public Response<ItemListResponseBySubQuery> getItemListBySubQuery(
            @PageableDefault(size = 12) Pageable pageable,
            @RequestParam(value = "sortcode", defaultValue = "recent") String sortcode,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {

        ItemListResponseBySubQuery itemListBySubQuery = itemService.getItemListBySubQuery(category, keyword, pageable, sortcode);
        return Response.success(HttpStatus.OK, "상품 목록 조회 성공!", itemListBySubQuery);
    }


    @GetMapping("/me")
    public Response<MyItemListResponse> getMyItemList(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        MyItemListResponse items = itemService.getMyItemList(pageable);
        return Response.success(HttpStatus.OK, "내가 등록한 아이템 조회 성공!", items);
    }

    @GetMapping("/bid/me")
    public Response<MyBidItemListResponse> getMyBidItemList(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        MyBidItemListResponse items = itemService.getMyBidItemList(pageable);
        return Response.success(HttpStatus.OK, "내가 입찰에 참가한 아이템 조회 성공!", items);
    }
    @LoginChecking
    @DeleteMapping("/{itemId}")
    public Response<Void> delete(@PathVariable(name = "itemId") Long itemId) {
        itemService.delete(itemId);
        return Response.success(HttpStatus.OK, "상품 삭제 완료!");
    }

    @LoginChecking
    @PatchMapping("/{itemId}/sold-out")
    public Response<Void> soldOut(@PathVariable(name = "itemId") Long itemId, @RequestParam(value = "buyerId") Long buyerId){
        itemService.soldOut(itemId, buyerId);
        return Response.success(HttpStatus.OK, "상품 판매 완료!");
    }

}
