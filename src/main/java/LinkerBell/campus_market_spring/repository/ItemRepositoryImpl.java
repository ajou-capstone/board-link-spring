package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.*;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.QItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
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
            //campus not fount exception 발생
        }

        // Item 검색 쿼리 생성
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
                        item.campus.campusId.eq(userCampusId), // 사용자의 campus와 item의 campus가 같은지 확인
                        item.isDeleted.eq(false) // 삭제되지 않은 항목만 조회
                )
                .groupBy(item.itemId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 여부를 확인하기 위해 +1 추가
                .orderBy(itemSearchSort(pageable));
         //정렬 적용
        List<ItemSearchResponseDto> content = query.fetch();

        // Slice 형태로 반환 처리
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(content.size() - 1); // 다음 페이지 확인 후 마지막 요소 제거
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
                    break;
                case "createdDate":
                    orderSpecifiers.add(order.isAscending() ? item.createdDate.asc() : item.createdDate.desc());
                    break;
                default:
                    // 잘못된 값이 들어오거나 정렬 조건이 없을 때 최신순으로 정렬
                    orderSpecifiers.add(item.createdDate.desc());
                    break;
            }
        }

        // 정렬이 비어 있을 경우 최신순으로 정렬
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(item.createdDate.desc());
        }

        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);


        }



    }

