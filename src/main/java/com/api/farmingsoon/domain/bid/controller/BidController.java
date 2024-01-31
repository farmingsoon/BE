package com.api.farmingsoon.domain.bid.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.dto.BidWithPageResponse;
import com.api.farmingsoon.domain.bid.dto.BidsResponse;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    public Response<Void> bid(@RequestBody BidRequest bidRequest) {
        bidService.bid(bidRequest);
        return Response.success(HttpStatus.OK, "입찰 등록 성공!");
    }



    @DeleteMapping("{bidId}")
    public Response<Void> delete(@PathVariable(name = "bidId") Long bidId) {
        bidService.delete(bidId);
        return Response.success(HttpStatus.OK, "입찰 삭제 성공!");
    }

    @DeleteMapping
    public Response<Void> deleteAll() {
        bidService.deleteAll();
        return Response.success(HttpStatus.OK, "전체 입찰 내역 삭제 성공!");
    }
}
