package LinkerBell.campus_market_spring.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminQaRequestDto(
    @NotBlank @Size(min = 2, max = 500) String answerDescription
) {

}
