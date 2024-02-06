package com.api.farmingsoon.domain.like.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.DuplicateException;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.like.repository.LikeableItemRepository;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeableItemService {

    private final LikeableItemRepository likeableItemRepository;
    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;

    @Transactional
    public void like(Long itemId) {
        Member member = authenticationUtils.getAuthenticationMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));

        // 해당 상품에 이미 좋아요를 누른 경우 예외 처리
        likeableItemRepository.findByMemberAndItem(member, item).ifPresent(it -> {
            throw new DuplicateException(ErrorCode.ALREADY_LIKED);
        });

        likeableItemRepository.save(LikeableItem.of(member, item));
    }

    @Transactional
    public void delete(Long itemId) {
        Member member = authenticationUtils.getAuthenticationMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        LikeableItem likeableItem = likeableItemRepository.findByMemberAndItem(member, item).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_LIKED));

        likeableItemRepository.delete(likeableItem);
    }
    @Transactional(readOnly = true)
    public ItemListResponse likableItemList(Pageable pageable) {
        Member member = authenticationUtils.getAuthenticationMember();
        Page<LikeableItem> likeableItems = likeableItemRepository.findAllByMember(member, pageable);
        return ItemListResponse.of(likeableItems.map(LikeableItem::getItem), member);
    }
}
