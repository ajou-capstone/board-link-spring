package LinkerBell.campus_market_spring.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimetableRequestDto {
    private List<Map<String, Object>> timetable;
}
