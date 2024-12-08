package LinkerBell.campus_market_spring.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AdminQaRequestDto {

    @NotBlank @Size(min = 2, max = 500) private String answerDescription;
}
