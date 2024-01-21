package com.api.farmingsoon.domain.item.controller;


import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.item.dto.ItemCreateRequest;
import com.api.farmingsoon.domain.item.dto.ItemResponse;
import com.api.farmingsoon.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Response<Void> createItem(@ModelAttribute @Valid ItemCreateRequest itemCreateRequest) throws IOException {
        itemService.createItem(itemCreateRequest);
        return Response.success(HttpStatus.OK, "상품 등록이 완료되었습니다.");
    }
    @GetMapping("/{itemId}")
    public Response<ItemResponse> getItemDetail(@Valid @PathVariable(value = "itemId") Long itemId) throws IOException {
        ItemResponse itemResponse = itemService.getItemDetail(itemId);
        return Response.success(HttpStatus.OK, String.format("%d번 상품 정보입니다.",itemId), itemResponse);
    }

    // TODO: 동민 작업 예정..
    @GetMapping
    public Response<Void> getItemList(
            @PageableDefault(size = 8) Pageable pageable,
            @RequestParam(value = "sortCode", defaultValue = "0") int sortCode,
            @RequestParam(value = "title", required = false) String title
    ) throws IOException {

        itemService.getItemList(pageable);
        return Response.success(HttpStatus.OK, "상품 목록 조회 성공!");
    }

    @DeleteMapping("/{itemId}")
    public Response<Void> delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return Response.success(HttpStatus.OK, "상품 삭제 완료!");
    }
}
