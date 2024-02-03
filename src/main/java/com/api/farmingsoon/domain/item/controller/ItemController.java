package com.api.farmingsoon.domain.item.controller;


import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.item.dto.ItemCreateRequest;
import com.api.farmingsoon.domain.item.dto.ItemResponse;
import com.api.farmingsoon.domain.item.dto.ItemWithPageResponse;
import com.api.farmingsoon.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public Response<ItemResponse> getItemDetail(@Valid @PathVariable(value = "itemId") Long itemId) {
        ItemResponse itemResponse = itemService.getItemDetail(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품 정보입니다.",itemId), itemResponse);
    }

    @GetMapping
    public Response<ItemWithPageResponse> getItemList(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {

        ItemWithPageResponse items = itemService.getItemList(category, keyword, pageable);
        return Response.success(HttpStatus.OK, "상품 목록 조회 성공!", items);
    }
    @GetMapping("/bid/me")
    public Response<ItemWithPageResponse> getMyBidItemList(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        ItemWithPageResponse items = itemService.getMyBidItemList(pageable);
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
