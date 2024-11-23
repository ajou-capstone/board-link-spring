package LinkerBell.campus_market_spring.admin.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserReportRequestDto(
    @NotNull Boolean isSuspended,
    String suspendReason,
    Integer suspendPeriod
) {

}
