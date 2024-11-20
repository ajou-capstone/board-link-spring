package LinkerBell.campus_market_spring.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewRequestDto {

    private Long itemId;
    private String description;
    private int rating;
}
