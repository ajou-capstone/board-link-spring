package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemReportRequestDto {

    @NotNull
    private ItemReportCategory category;

    @NotBlank
    @Min(2) @Max(1000)
    private String description;
}
