package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserReportResponseDto(
    Long userReportId,
    Long userId,
    Long targetId,
    String description,
    UserReportCategory category,
    @JsonProperty(value = "isCompleted") Boolean isCompleted
) {

    public UserReportResponseDto(UserReport userReport) {
        this(userReport.getUserReportId(), userReport.getUser().getUserId(),
            userReport.getTarget().getUserId(), userReport.getDescription(),
            userReport.getCategory(), userReport.isCompleted());
    }
}
