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
public class UserReportResponseDto {

    private Long userReportId;
    private Long userId;
    private Long targetId;
    private String description;
    private UserReportCategory category;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;

    public UserReportResponseDto(UserReport userReport) {
        this(userReport.getUserReportId(), userReport.getUser().getUserId(),
            userReport.getTarget().getUserId(), userReport.getDescription(),
            userReport.getCategory(), userReport.isCompleted());
    }
}
