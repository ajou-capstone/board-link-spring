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
    @JsonProperty(value = "isSuspended") private Boolean isSuspended;
    private String suspendReason;
    private Integer suspendPeriod;

    public UserReportResponseDto(UserReport userReport) {
        this.userReportId = userReport.getUserReportId();
        this.userId = userReport.getUser().getUserId();
        this.targetId = userReport.getTarget().getUserId();
        this.description = userReport.getDescription();
        this.category = userReport.getCategory();
        this.isCompleted = userReport.isCompleted();
        this.isSuspended = userReport.isSuspended();

        if (this.isSuspended) {
            this.suspendReason = userReport.getSuspendReason();
            this.suspendPeriod = userReport.getSuspendPeriod();
        }
    }
}
