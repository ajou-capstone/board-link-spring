package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.Campus;
import lombok.Data;

@Data
public class AdminCampusResponseDto {

    private Long campusId;
    private String campusName;

    public AdminCampusResponseDto(Campus campus) {
        this.campusId = campus.getCampusId();
        this.campusName = campus.getUniversityName() + " " + campus.getRegion();
    }
}
