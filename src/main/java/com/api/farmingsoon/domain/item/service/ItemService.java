package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.ForbiddenException;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.s3.service.S3Service;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.image.service.ImageService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.dto.ItemCreateRequest;
import com.api.farmingsoon.domain.item.dto.ItemResponse;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public void createItem(ItemCreateRequest itemCreateRequest) {

        Item item = itemRepository.save(itemCreateRequest.toEntity());
        String thumbnailImageUrl = imageService.uploadAndCreateItemImages(item, itemCreateRequest.getThumbnailImage(), itemCreateRequest.getImages(), "Item");

        item.setThumbnailImageUrl(thumbnailImageUrl);
    }

    // TODO:
    public getItemList(Pageable pageable) {
        // TODO
    }

    public ItemResponse getItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        return ItemResponse.fromEntity(item);
    }

    @Transactional
    public void delete(Long itemId) {
        String email = AuthenticationUtils.getEmail();

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));

        if (item.getMember() != member) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_DELETE);
        }

        itemRepository.deleteById(itemId);
    }

}
