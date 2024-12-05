package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.QChatRoom;
import LinkerBell.campus_market_spring.domain.QItem;
import LinkerBell.campus_market_spring.domain.QLike;
import LinkerBell.campus_market_spring.domain.QUser;
import LinkerBell.campus_market_spring.dto.LikeSearchResponseDto;
import LinkerBell.campus_market_spring.dto.QItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LikeRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public SliceResponse<LikeSearchResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        QLike like = QLike.like;
        QItem item = QItem.item;
        QUser user = QUser.user;
        QChatRoom chatRoom = QChatRoom.chatRoom;

        JPAQuery<LikeSearchResponseDto> query = queryFactory
            .select(Projections.constructor(LikeSearchResponseDto.class,
                like.likeId,
                new QItemSearchResponseDto(
                    like.item.itemId,
                    like.item.user.userId,
                    like.item.user.nickname,
                    like.item.thumbnail,
                    like.item.title,
                    like.item.price,
                    chatRoom.countDistinct().intValue(),
                    like.count().intValue(),
                    like.item.itemStatus,
                    Expressions.TRUE, like.item.createdDate, like.item.lastModifiedDate)
            ))
            .from(like)
            .leftJoin(like.item, item)
            .leftJoin(item.user, user)
            .leftJoin(chatRoom).on(chatRoom.item.eq(like.item))
            .where(
                like.user.userId.eq(userId),
                item.isDeleted.isFalse()
            )
            .groupBy(like.likeId)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(like.createdDate.desc());

        List<LikeSearchResponseDto> content = query.fetch();

        content.forEach(likeResponseDto -> {
            Integer likeCount = queryFactory
                .select(like.countDistinct().intValue())
                .from(item)
                .where(item.itemId.eq(likeResponseDto.getItem().getItemId()))
                .leftJoin(like).on(like.item.eq(item))
                .groupBy(item.itemId)
                .fetch().get(0);
            likeResponseDto.getItem().setLikeCount(likeCount);
        });

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(content.size() - 1);
            hasNext = true;
        }
        return new SliceResponse<LikeSearchResponseDto>(
            new SliceImpl<>(content, pageable, hasNext));
    }
}
