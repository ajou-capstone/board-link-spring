package LinkerBell.campus_market_spring.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminUserReportRequestDto {

    @NotNull
    private Boolean isSuspended;
    private String suspendReason;
    private Integer suspendPeriod;
}
