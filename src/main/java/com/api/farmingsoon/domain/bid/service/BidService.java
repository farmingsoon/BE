package com.api.farmingsoon.domain.bid.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.dto.BidWithPageResponse;
import com.api.farmingsoon.domain.bid.dto.BidsResponse;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.bid.model.BidResult;
import com.api.farmingsoon.domain.bid.repository.BidRepository;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;

    public void bid(BidRequest bidRequest) {
        Member member = authenticationUtils.getAuthenticationMember();
        Item item = itemRepository.findById(bidRequest.getItemId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));

        bidRepository.save(Bid.of(item, member, bidRequest.getPrice(), BidResult.BIDDING));
    }

    public void delete(Long bidId) {
        Member member = authenticationUtils.getAuthenticationMember();
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BID));

        bidRepository.delete(bid);
    }

    public void deleteAll() {
        Member member = authenticationUtils.getAuthenticationMember();
        bidRepository.deleteAllByMember(member);
    }

    public Page<Bid> getMyBidList(Member member, Pageable pageable){
        return bidRepository.findAllByMember(member, pageable);
    }
}
