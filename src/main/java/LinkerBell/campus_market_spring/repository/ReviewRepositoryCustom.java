package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.dto.ReviewResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    SliceResponse<ReviewResponseDto> findAllByUserId(Long userId, Pageable pageable);
}
