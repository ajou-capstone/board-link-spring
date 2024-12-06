package LinkerBell.campus_market_spring.admin.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCampusesResponseDto {

    private List<AdminCampusResponseDto> campuses;
}
