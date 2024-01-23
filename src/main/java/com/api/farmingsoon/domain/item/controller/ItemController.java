package com.api.farmingsoon.domain.item.controller;


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
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Response<Void> createItem(@ModelAttribute @Valid ItemCreateRequest itemCreateRequest) {
        itemService.createItem(itemCreateRequest);
        return Response.success(HttpStatus.OK, "상품 등록이 완료되었습니다.");
    }
    @GetMapping("/{itemId}")
    public Response<ItemResponse> getItemDetail(@Valid @PathVariable(value = "itemId") Long itemId) {
        ItemResponse itemResponse = itemService.getItemDetail(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품 정보입니다.",itemId), itemResponse);
    }

    // TODO: 동민 작업 예정..
    @GetMapping
    public Response<ItemWithPageResponse> getItemList(
            // query parameter에 정렬 조건이 없는 경우 생성일 기준 내림차순 정렬
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {

        ItemWithPageResponse items = itemService.getItemList(category, keyword, pageable);
        return Response.success(HttpStatus.OK, "상품 목록 조회 성공!", items);
    }

    @DeleteMapping("/{itemId}")
    public Response<Void> delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return Response.success(HttpStatus.OK, "상품 삭제 완료!");
    }
}