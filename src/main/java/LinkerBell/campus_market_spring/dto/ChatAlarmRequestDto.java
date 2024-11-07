package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChatAlarmRequestDto {

    @JsonProperty(value = "isAlarm")
    private Boolean isAlarm;
}
