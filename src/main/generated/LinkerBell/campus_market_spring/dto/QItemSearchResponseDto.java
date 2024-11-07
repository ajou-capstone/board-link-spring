package LinkerBell.campus_market_spring.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * LinkerBell.campus_market_spring.dto.QItemSearchResponseDto is a Querydsl Projection type for ItemSearchResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemSearchResponseDto extends ConstructorExpression<ItemSearchResponseDto> {

    private static final long serialVersionUID = -1184577670L;

    public QItemSearchResponseDto(com.querydsl.core.types.Expression<Long> itemId, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> nickname, com.querydsl.core.types.Expression<String> thumbnail, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<Integer> price, com.querydsl.core.types.Expression<Integer> chatCount, com.querydsl.core.types.Expression<Integer> likeCount, com.querydsl.core.types.Expression<LinkerBell.campus_market_spring.domain.ItemStatus> itemStatus) {
        super(ItemSearchResponseDto.class, new Class<?>[]{long.class, long.class, String.class, String.class, String.class, int.class, int.class, int.class, LinkerBell.campus_market_spring.domain.ItemStatus.class}, itemId, userId, nickname, thumbnail, title, price, chatCount, likeCount, itemStatus);
    }

}

