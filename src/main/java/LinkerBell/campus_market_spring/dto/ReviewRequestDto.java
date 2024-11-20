package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewRequestDto {

    private Long itemId;
    @Size(max = 500)
    private String description;
    @Min(0)
    @Max(10)
    private int rating;
}
