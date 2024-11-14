package LinkerBell.campus_market_spring.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimetableResponseDto {
    private List<Map<String, Object>> timetable;

}
