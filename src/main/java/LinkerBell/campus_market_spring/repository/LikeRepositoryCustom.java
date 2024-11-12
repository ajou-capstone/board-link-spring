package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.LikeSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LikeRepositoryCustom {
    SliceResponse<LikeSearchResponseDto> findAllByUserId(Long userId, Pageable pageable);
}
