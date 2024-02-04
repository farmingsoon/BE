package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.api.farmingsoon.domain.item.domain.QItem.item;
import static com.api.farmingsoon.domain.member.model.QMember.member;

@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> findItemList(String category, String keyword, Pageable pageable) {
        List<Item> content = queryFactory
                .selectFrom(item)
                .innerJoin(item.member, member)
                .fetchJoin()
                .where(eqCategory(category), containsKeyword(keyword))
                .orderBy(getAllOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(item.count())
                .from(item)
                .innerJoin(item.member, member)
                .where(eqCategory(category), containsKeyword(keyword))
                .fetchOne();


        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression eqCategory(String category) {
        log.debug("카테고리: {}", category);
        return category != null ? item.category.eq(category) : null;
    }

    private BooleanExpression containsKeyword(String keyword) {
        log.debug("검색어: {}", keyword);
        return keyword != null ? item.title.contains(keyword) : null;
    }

    private OrderSpecifier<?>[] getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            log.debug("정렬기준: {}", direction);
            log.debug("정렬조건: {}", order.getProperty());

            switch (order.getProperty()) {
                case "recent" -> orderSpecifierList.add(new OrderSpecifier<>(direction, item.createdAt));
                case "hot" -> orderSpecifierList.add(new OrderSpecifier<>(direction, item.viewCount));
                case "highest", "lowest" -> orderSpecifierList.add(new OrderSpecifier<>(direction, item.bidList.any().price.max()));
                //case "expiredAt" -> orderSpecifierList.add(new OrderSpecifier<>(direction, item.expiredAt));
            }
        }

        return orderSpecifierList.toArray(OrderSpecifier[]::new);
    }
}