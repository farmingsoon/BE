package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.api.farmingsoon.domain.bid.model.QBid.bid;
import static com.api.farmingsoon.domain.item.domain.QItem.item;

@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Item> findItemList(String category, String keyword, Pageable pageable, String sortCode, String itemStatus) {
        List<Item> content = queryFactory
                .selectFrom(item)
                .leftJoin(item.bidList, bid)
                .where(eqCategory(category), containsKeyword(keyword), eqItemStatus(itemStatus))
                .groupBy(item.id)
                .orderBy(getAllOrderSpecifiers(sortCode))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(item.count())
                .from(item)
                .where(eqCategory(category), containsKeyword(keyword), eqItemStatus(itemStatus))
                .fetchOne();


        return new PageImpl<>(content, pageable, total);
    }
    @Override
    public List<Item> findNotEndBidItemList() {
        return queryFactory.selectFrom(item)
                .where(item.itemStatus.eq(ItemStatus.BIDDING), item.expiredAt.before(LocalDateTime.now()))
                .fetch();
    }

    @Override
    public List<Item> findBiddingItemList() {
        return queryFactory.selectFrom(item)
                .where(item.itemStatus.eq(ItemStatus.BIDDING), item.expiredAt.after(LocalDateTime.now()))
                .fetch();
    }

    private BooleanExpression eqItemStatus(String itemStatus) {
        log.debug("상품 상태: {}", itemStatus);
        if (itemStatus == null)
            return null;
        switch (itemStatus){
            case "BIDDING" -> {
                return item.itemStatus.eq(ItemStatus.BIDDING);
            }
            case "BID_END" -> {
                return item.itemStatus.eq(ItemStatus.BID_END);
            }
            case "SOLDOUT" -> {
                return item.itemStatus.eq(ItemStatus.SOLDOUT);
            }

        }
        return null;
    }

    private BooleanExpression eqCategory(String category) {
        log.debug("카테고리: {}", category);
        return category != null ? item.category.eq(category) : null;
    }

    private BooleanExpression containsKeyword(String keyword) {
        log.debug("검색어: {}", keyword);
        return keyword != null ? item.title.contains(keyword) : null;
    }

    private OrderSpecifier<?>[] getAllOrderSpecifiers(String sortcode) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        switch (sortcode) {
                case "recent"-> orderSpecifierList.add(new OrderSpecifier<>(Order.DESC, item.createdAt));
                case "hot" -> orderSpecifierList.add(new OrderSpecifier<>(Order.DESC, item.viewCount));
                case "imminent" -> orderSpecifierList.add(new OrderSpecifier<>(Order.ASC, item.expiredAt));
                case "highest" -> orderSpecifierList.add(new OrderSpecifier<>(Order.DESC, item.awardPrice));
                case "lowest" -> orderSpecifierList.add(new OrderSpecifier<>(Order.ASC, item.awardPrice));
                //case "expiredAt" -> orderSpecifierList.add(new OrderSpecifier<>(direction, item.expiredAt));
        }


        return orderSpecifierList.toArray(OrderSpecifier[]::new);
    }
}