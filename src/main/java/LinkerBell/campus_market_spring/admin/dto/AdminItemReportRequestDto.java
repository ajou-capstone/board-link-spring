package LinkerBell.campus_market_spring.admin.dto;

import jakarta.validation.constraints.NotNull;

public record AdminItemReportRequestDto(
    @NotNull(message = "삭제 처리 여부가 필요합니다.") Boolean isDeleted
) {

}
