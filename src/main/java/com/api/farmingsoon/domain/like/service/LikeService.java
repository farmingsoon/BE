package com.api.farmingsoon.domain.like.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.DuplicateException;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.like.model.Like;
import com.api.farmingsoon.domain.like.repository.LikeRepository;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;

    @Transactional
    public void like(Long itemId) {
        Member member = authenticationUtils.getAuthenticationMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));

        // 해당 상품에 이미 좋아요를 누른 경우 예외 처리
        likeRepository.findByMemberAndItem(member, item).ifPresent(it -> {
            throw new DuplicateException(ErrorCode.ALREADY_LIKED);
        });

        likeRepository.save(Like.of(member, item));
    }

    @Transactional
    public void delete(Long itemId) {
        Member member = authenticationUtils.getAuthenticationMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        Like like = likeRepository.findByMemberAndItem(member, item).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_LIKED));

        likeRepository.delete(like);
    }

    public ItemListResponse likedItemList(Pageable pageable) {
        Member member = authenticationUtils.getAuthenticationMember();

        List<Long> likedItemIds = likeRepository.findAllByMember(member)
                .stream()
                .map(like -> like.getItem().getId())
                .toList();

        return ItemListResponse.of(itemRepository.findAllByIdIn(likedItemIds, pageable));
    }

    public long likeCount(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        return likeRepository.countByItem(item);
    }
}
