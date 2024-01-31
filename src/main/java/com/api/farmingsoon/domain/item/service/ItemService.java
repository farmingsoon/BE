package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.common.event.ItemSoldOutEvent;
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
import com.api.farmingsoon.domain.item.dto.ItemCreateRequest;
import com.api.farmingsoon.domain.item.dto.ItemResponse;
import com.api.farmingsoon.domain.item.dto.ItemWithPageResponse;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final AuthenticationUtils authenticationUtils;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;
    private final BidService bidService;

    /**
     * @Description
     * DB에 이미지 Url을 저장, S3업로드를 동기화 시키기 위함
     * 모든 이미지를 S3에 성공적으로 업로드한 뒤 이벤트를 발행
     * -> 이 때 발생하는 이벤트는 트랜잭션 도중 Rollback이 될 경우 동작하는 이벤트
     * S3업로드 로직에 문제가 없을 시 DB에 저장
     *
     * S3서비스를 ImageService에서만 사용하도록 넘김
     */
    public void createItem(ItemCreateRequest itemCreateRequest) {
        List<String> imageUrls = imageService.uploadItemImages(itemCreateRequest.getThumbnailImage(), itemCreateRequest.getImages());
        saveItemAndImage(itemCreateRequest.toEntity(), imageUrls);
    }

    @Transactional
    public void saveItemAndImage(Item item, List<String> imageUrls) {
        eventPublisher.publishEvent(new UploadImagesRollbackEvent(imageUrls));

        item.setMember(authenticationUtils.getAuthenticationMember());
        item.setThumbnailImageUrl(imageUrls.get(0));
        itemRepository.save(item);
        imageUrls.stream().skip(1).forEach
                (
                    imageUrl -> imageService.createImage(Image.of(imageUrl, item))
                );
    }

    public ItemWithPageResponse getItemList(String category, String keyword, Pageable pageable) {
        return ItemWithPageResponse.of(itemRepository.findItemList(category, keyword, pageable));
    }

    public ItemResponse getItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        return ItemResponse.fromEntity(item);
    }

    @Transactional
    public void delete(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));

        AuthenticationUtils.checkDeletePermission(item.getMember());

        itemRepository.deleteById(itemId);
    }
    public Item getItemById(Long itemId){
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
    }


    public ItemWithPageResponse getMyBidItemList(Pageable pageable) {
        Page<Bid> myBidList = bidService.getMyBidList(authenticationUtils.getAuthenticationMember(), pageable);

        return ItemWithPageResponse.of(myBidList.map(Bid::getItem));
    }

    @Transactional
    public void soldOut(Long itemId, Long buyerId) {
        /**
         *  @Todo 이 부분 고민좀 해봐야 할 것 같아서 일단 여기까지만 작업하겠습니다.
         *  낙찰자와 입찰 실패한 사람에게 따로 알림을 보내야함 분기처리 애매
         */

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        AuthenticationUtils.checkUpdatePermission(item.getMember());

        List<Bid> bidList = item.getBidList();
        for(Bid bid : bidList)
        {
            if(bid.getMember().getId() == buyerId)
                bid.updateBidResult(BidResult.BID_SUCCESS);
            else
                bid.updateBidResult(BidResult.BID_FAIL);
        }

        item.updateItemStatus(ItemStatus.SOLDOUT);
        eventPublisher.publishEvent(new ItemSoldOutEvent(item));
    }
}
