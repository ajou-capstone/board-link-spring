package LinkerBell.campus_market_spring.repository;

import static LinkerBell.campus_market_spring.domain.QItem.item;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.QCampus;
import LinkerBell.campus_market_spring.domain.QChatRoom;
import LinkerBell.campus_market_spring.domain.QItem;
import LinkerBell.campus_market_spring.domain.QItemPhotos;
import LinkerBell.campus_market_spring.domain.QLike;
import LinkerBell.campus_market_spring.domain.QUser;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public SliceResponse<ItemSearchResponseDto> itemSearch(Long campusId, String name,
        Category category, Integer minPrice, Integer maxPrice, Pageable pageable) {
        QItem item = QItem.item;
        QUser user = QUser.user;
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QLike like = QLike.like;

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
                item.campus.campusId.eq(campusId),
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
        return new SliceResponse<ItemSearchResponseDto>(
            new SliceImpl<>(content, pageable, hasNext));
    }

    @Override
    public ItemDetailsViewResponseDto findByItemDetails(Long userId, Long itemId) {
        QItem item = QItem.item;
        QUser user = QUser.user;
        QCampus campus = QCampus.campus;
        QLike like = QLike.like;
        QItemPhotos itemPhotos = QItemPhotos.itemPhotos;
        QChatRoom chatRoom = QChatRoom.chatRoom;

        Item itemEntity = queryFactory
            .selectFrom(item)
            .join(item.user, user).fetchJoin()
            .join(item.campus, campus).fetchJoin()
            .where(item.itemId.eq(itemId))
            .fetchOne();

        List<String> images = queryFactory
            .select(itemPhotos.imageAddress)
            .from(itemPhotos)
            .where(itemPhotos.item.itemId.eq(itemId))
            .fetch();

        Integer chatCount = queryFactory
            .select(chatRoom.countDistinct().intValue())
            .from(chatRoom)
            .where(chatRoom.item.itemId.eq(itemId))
            .fetchOne();
        if (chatCount == null) {
            chatCount = 0;
        }

        List<Like> likeEntity = queryFactory
            .selectFrom(like)
            .join(like.user, user).fetchJoin()
            .where(like.item.itemId.eq(itemId))
            .fetch();

        int likeCount = likeEntity.size();
        boolean isLiked = likeEntity.stream()
            .anyMatch(injectLike -> injectLike.getUser().getUserId().equals(userId));

        return itemDetailsToItemDetailsViewResponseDto(itemEntity, images, chatCount, likeCount,
            isLiked);
    }

    private ItemDetailsViewResponseDto itemDetailsToItemDetailsViewResponseDto(Item itemEntity,
        List<String> images, Integer chatCount, Integer likeCount, boolean isLiked) {
        return ItemDetailsViewResponseDto.builder()
            .itemId(itemEntity.getItemId())
            .userId(itemEntity.getUser().getUserId())
            .campusId(itemEntity.getCampus().getCampusId())
            .nickname(itemEntity.getUser().getNickname())
            .title(itemEntity.getTitle())
            .description(itemEntity.getDescription())
            .price(itemEntity.getPrice())
            .category(itemEntity.getCategory())
            .thumbnail(itemEntity.getThumbnail())
            .images(images)
            .chatCount(chatCount)
            .likeCount(likeCount)
            .isLiked(isLiked)
            .itemStatus(itemEntity.getItemStatus())
            .build();
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
                    orderSpecifiers.add(
                        order.isAscending() ? item.createdDate.asc() : item.createdDate.desc());
                    break;
                default:
                    throw new CustomException(ErrorCode.INVALID_SORT);
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()]);
    }
}

