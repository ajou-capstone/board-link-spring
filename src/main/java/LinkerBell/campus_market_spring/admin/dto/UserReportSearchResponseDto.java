package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReportSearchResponseDto {

    private Long userReportId;
    private Long userId;
    private Long targetId;
    private UserReportCategory category;
    private String description;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;

    public UserReportSearchResponseDto(UserReport userReport) {
        this(userReport.getUserReportId(), userReport.getUser().getUserId(),
            userReport.getTarget().getUserId(), userReport.getCategory(),
            userReport.getDescription(), userReport.isCompleted());
    }
}
