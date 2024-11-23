package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.QItem;
import LinkerBell.campus_market_spring.domain.QReview;
import LinkerBell.campus_market_spring.domain.QUser;
import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public SliceResponse<ReviewResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        QReview review = QReview.review;
        QUser user = QUser.user;
        QItem item = QItem.item;

        JPAQuery<ReviewResponseDto> query = queryFactory
            .select(Projections.constructor(ReviewResponseDto.class,
                review.reviewId,
                review.user.nickname,
                review.user.profileImage,
                review.description,
                review.rating,
                review.createdDate))
            .from(review)
            .leftJoin(review.user, user)
            .leftJoin(review.item, item)
            .where(review.user.userId.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(review.createdDate.desc(), review.reviewId.desc());

        List<ReviewResponseDto> reviews = query.fetch();
        boolean hasNext = false;
        if (reviews.size() > pageable.getPageSize()) {
            reviews.remove(reviews.size() - 1);
            hasNext = true;
        }

        return new SliceResponse<>(new SliceImpl<>(reviews, pageable, hasNext));
    }
}
