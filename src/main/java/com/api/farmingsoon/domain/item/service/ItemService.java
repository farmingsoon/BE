package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.util.Transaction;
import com.api.farmingsoon.domain.item.dto.*;
import com.api.farmingsoon.domain.item.event.ItemSaveEvent;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.image.event.UploadImagesRollbackEvent;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;
    private final BidService bidService;
    private final NotificationService notificationService;
    private final ItemRedisService itemRedisService;
    private final Transaction transaction;
    private final RedisService redisService;
    /**
     * @Description
     * DB에 이미지 Url을 저장, S3업로드를 동기화 시키기 위함
     * 모든 이미지를 S3에 성공적으로 업로드한 뒤 이벤트를 발행
     * -> 이 때 발생하는 이벤트는 트랜잭션 도중 Rollback이 될 경우 동작하는 이벤트
     * S3업로드 로직에 문제가 없을 시 DB에 저장
     * DB에 성공적으로 저장됐다면 이후 입찰만료 ItemEventListener 에서 ItemSaveEvent 를 잡아 입찰 기간을 설정하는 코드 수행
     *
     */
    public Long createItem(ItemCreateRequest itemCreateRequest) {
        List<String> imageUrls = imageService.uploadItemImages(itemCreateRequest.getThumbnailImage(), itemCreateRequest.getImages());
        return saveItemAndImage(itemCreateRequest.toEntity(), imageUrls);
    }


    public Long saveItemAndImage(Item item, List<String> imageUrls) {
         return transaction.invoke(() ->
                {
                    eventPublisher.publishEvent(new UploadImagesRollbackEvent(imageUrls));

                    item.setMember(authenticationUtils.getAuthenticationMember());
                    item.setThumbnailImageUrl(imageUrls.get(0));
                    Long itemId = itemRepository.save(item).getId();

                    imageUrls.stream().skip(1).forEach
                            (
                                    imageUrl -> imageService.createImage(Image.of(imageUrl, item)));
                    eventPublisher.publishEvent(new ItemSaveEvent(itemId, item.getBidPeriod()));
                    return itemId;
                }
        );
    }
    @Transactional(readOnly = true)
    public ItemListResponse getItemList(String category, String keyword, Pageable pageable, String sortcode) {
        Optional<Member> viewer = authenticationUtils.getOptionalMember();
        Page<Item> itemList = itemRepository.findItemList(category, keyword, pageable, sortcode);

        return ItemListResponse.of(itemList, viewer);
    }
    @Transactional(readOnly = true)
    public ItemDetailResponse getItemDetail(Long itemId) {
        Optional<Member> viewer = authenticationUtils.getOptionalMember();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        return ItemDetailResponse.of(item, viewer);
    }
    public void handleViewCount(Long itemId, String cookieValue) {
        itemRedisService.handleViewCount(cookieValue, itemId);
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
        log.info("입찰 마감 - item_" + item.getId());
        notificationService.createAndSendBidEndNotification(item);
    }

    @Transactional
    public void increaseViewCount(Long itemId, Integer viewCount) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        item.increaseViewCount(viewCount);
    }

    @Transactional
    public void bidEndByScheduler(){
        List<Item> itemList = itemRepository.findNotEndBidItemList();
        itemList.forEach(item -> bidEnd(item.getId()));
    }

    @Transactional(readOnly = true)
    public List<Item> findBiddingItemList(){
        return itemRepository.findBiddingItemList();
    }

    public ItemListResponseBySubQuery getItemListBySubQuery(String category, String keyword, Pageable pageable, String sortcode) {
        Page<ItemResponseBySubQuery> itemResponseList = itemRepository.findItemResponseList(category, keyword, pageable, sortcode);
        return new ItemListResponseBySubQuery(Pagination.of(itemResponseList), itemResponseList.getContent());

    }
}
