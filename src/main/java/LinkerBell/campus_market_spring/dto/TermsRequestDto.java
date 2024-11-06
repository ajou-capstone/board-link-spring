package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TermsRequestDto {

    private Long id;

    @JsonProperty(value = "isAgree")
    private boolean isAgree;
}
