package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserReportSearchResponseDto(
    Long userReportId,
    Long userId,
    Long targetId,
    UserReportCategory category,
    String description,
    @JsonProperty(value = "isCompleted") Boolean isCompleted
) {

    public UserReportSearchResponseDto(UserReport userReport) {
        this(userReport.getUserReportId(), userReport.getUser().getUserId(),
            userReport.getTarget().getUserId(), userReport.getCategory(),
            userReport.getDescription(), userReport.isCompleted());
    }
}
