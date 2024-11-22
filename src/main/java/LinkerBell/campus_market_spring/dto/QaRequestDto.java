package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QaCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QaRequestDto(
    @NotBlank @Size(min = 2, max = 50, message = "문의 제목은 2자 이상 50자 이하입니다.") String title,
    @NotNull QaCategory category,
    @NotBlank @Size(min = 2, max = 500, message = "문의 내용은 2자 이상 500 이하입니다.") String description) {

}
