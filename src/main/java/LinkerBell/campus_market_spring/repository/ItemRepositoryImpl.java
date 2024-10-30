package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.*;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.QItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static LinkerBell.campus_market_spring.domain.QItem.item;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public SliceResponse<ItemSearchResponseDto> itemSearch(Long userId, String name, Category category, Integer minPrice, Integer maxPrice, Pageable pageable) {
        QItem item = QItem.item;
        QUser user = QUser.user;
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QLike like = QLike.like;

        Long userCampusId = queryFactory
                .select(user.campus.campusId)
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();

        if (userCampusId == null) {
            throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
        }

        JPAQuery<ItemSearchResponseDto> query = queryFactory
                .select(new QItemSearchResponseDto(
                        item.itemId,
                        item.user.userId,
                        item.user.nickname,
                        item.thumbnail,
                        item.title,
                        item.price,
                        chatRoom.countDistinct().intValue(),
                        like.countDistinct().intValue(),
                        item.itemStatus
                ))
                .from(item)
                .leftJoin(item.user, user)
                .leftJoin(chatRoom).on(chatRoom.item.eq(item))
                .leftJoin(like).on(like.item.eq(item))
                .where(
                        itemNameContains(name),
                        itemCategoryEq(category),
                        itemPriceBetween(minPrice, maxPrice),
                        item.campus.campusId.eq(userCampusId),
                        item.isDeleted.eq(false)
                )
                .groupBy(item.itemId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(itemSearchSort(pageable));

        List<ItemSearchResponseDto> content = query.fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(content.size() - 1);
            hasNext = true;
        }
        return new SliceResponse<ItemSearchResponseDto>(new SliceImpl<>(content, pageable, hasNext));
    }

    private BooleanExpression itemNameContains(String name) {
        return name != null ? item.title.containsIgnoreCase(name) : null;
    }

    private BooleanExpression itemCategoryEq(Category category) {
        return category != null ? item.category.eq(category) : null;
    }

    private BooleanExpression itemPriceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return item.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return item.price.goe(minPrice);
        } else if (maxPrice != null) {
            return item.price.loe(maxPrice);
        }
        return null;
    }

    private OrderSpecifier<?>[] itemSearchSort(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            switch (order.getProperty()) {
                case "price":
                    orderSpecifiers.add(order.isAscending() ? item.price.asc() : item.price.desc());
                    orderSpecifiers.add(item.createdDate.desc());
                    break;
                case "createdDate":
                    orderSpecifiers.add(order.isAscending() ? item.createdDate.asc() : item.createdDate.desc());
                    break;
                default:
                    throw new CustomException(ErrorCode.INVALID_SORT);
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()]);
    }
}

