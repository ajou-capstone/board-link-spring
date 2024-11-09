package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemStatusChangeRequestDto {

    @NotNull
    private ItemStatus itemStatus;
    @NotNull
    private Long buyerId;
}
