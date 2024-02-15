package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.domain.item.dto.*;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.common.event.UploadImagesRollbackEvent;
import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.bid.model.BidResult;
import com.api.farmingsoon.domain.bid.service.BidService;
import com.api.farmingsoon.domain.image.domain.Image;
import com.api.farmingsoon.domain.image.service.ImageService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;
    private final BidService bidService;
    private final NotificationService notificationService;
    private final ItemRedisService itemRedisService;

    /**
     * @Description
     * DB에 이미지 Url을 저장, S3업로드를 동기화 시키기 위함
     * 모든 이미지를 S3에 성공적으로 업로드한 뒤 이벤트를 발행
     * -> 이 때 발생하는 이벤트는 트랜잭션 도중 Rollback이 될 경우 동작하는 이벤트
     * S3업로드 로직에 문제가 없을 시 DB에 저장
     *
     * S3서비스를 ImageService에서만 사용하도록 넘김
     */
    public Long createItem(ItemCreateRequest itemCreateRequest) {
        List<String> imageUrls = imageService.uploadItemImages(itemCreateRequest.getThumbnailImage(), itemCreateRequest.getImages());
        return saveItemAndImage(itemCreateRequest.toEntity(), imageUrls);
    }

    @Transactional
    public Long saveItemAndImage(Item item, List<String> imageUrls) {
        eventPublisher.publishEvent(new UploadImagesRollbackEvent(imageUrls));

        item.setMember(authenticationUtils.getAuthenticationMember());
        item.setThumbnailImageUrl(imageUrls.get(0));
        Long itemId = itemRepository.save(item).getId();

        imageUrls.stream().skip(1).forEach
                (
                    imageUrl -> imageService.createImage(Image.of(imageUrl, item))
                );
        itemRedisService.setBidEndTime(item.getId(), item.getBidPeriod());
        return itemId;
    }
    @Transactional(readOnly = true)
    public ItemListResponse getItemList(String category, String keyword, Pageable pageable, String sortcode) {
        Optional<Member> viewer = authenticationUtils.getOptionalMember();
        return ItemListResponse.of(itemRepository.findItemList(category, keyword, pageable, sortcode), viewer);
    }
    @Transactional(readOnly = true)
    public ItemDetailResponse getItemDetail(Long itemId) {
        Optional<Member> viewer = authenticationUtils.getOptionalMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        return ItemDetailResponse.of(item, viewer);
    }

    @Transactional
    public void delete(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        Member member = item.getMember();
        AuthenticationUtils.checkDeletePermission(member.getEmail(), member.getRole());

        itemRepository.deleteById(itemId);
    }
    @Transactional(readOnly = true)
    public Item getItemById(Long itemId){
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
    }
    @Transactional(readOnly = true)
    public MyItemListResponse getMyItemList(Pageable pageable) {
        return MyItemListResponse.of(itemRepository.findAllByMember(authenticationUtils.getAuthenticationMember(), pageable));
    }

    @Transactional(readOnly = true)
    public MyBidItemListResponse getMyBidItemList(Pageable pageable) {
        Member viewer = authenticationUtils.getAuthenticationMember();
        Page<Bid> myBidList = bidService.getMyBidList(viewer, pageable);

        return MyBidItemListResponse.of(myBidList.map(Bid::getItem), viewer);
    }

    /**
     * @Description
     * - 아이템 판매 완료 처리
     * - 각 입찰에 대해 결과 반영
     * - 알림 저장 및 전송 로직은 별도의 트랜잭션에서 진행
     */
    @Transactional
    public void soldOut(Long itemId, Long buyerId) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        Member member = item.getMember();
        AuthenticationUtils.checkUpdatePermission(member.getEmail(), member.getRole());

        item.updateItemStatus(ItemStatus.SOLDOUT);

        List<Member> bidderList = new ArrayList<>();

        for(Bid bid : item.getBidList())
        {
            if(bid.getMember().getId().equals(buyerId)) {
                bid.updateBidResult(BidResult.BID_SUCCESS);
                bidderList.add(0,bid.getMember());
            }
            else {
                bid.updateBidResult(BidResult.BID_FAIL);
                bidderList.add(bid.getMember());
            }
        }

        notificationService.createAndSendSoldOutNotification(bidderList, item);
    }

    @Transactional
    public void bidEnd(Long itemId)
    {
        Item item = getItemById(itemId);
        item.updateItemStatus(ItemStatus.BID_END);
        notificationService.createAndSendBidEndNotification(item);
    }


}
