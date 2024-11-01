package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.CampusResponseDto;
import java.util.List;

public record CampusCollectionResponseDto (List<CampusResponseDto> campuses) {
    public static CampusCollectionResponseDto from(List<CampusResponseDto> campuses) {
        return new CampusCollectionResponseDto(campuses);
    }
}
