package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.UserReportCategory;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserReportCategoryResponseDto {

    private UserReportCategory[] categories;
}
