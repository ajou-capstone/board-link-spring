package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.UserReportCategory;
import java.io.Serializable;

public record UserReportCategoryResponseDto(UserReportCategory[] categories) implements
    Serializable {

}
