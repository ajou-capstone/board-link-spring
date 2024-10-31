package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TermsResponseDto {
    private Long id;
    private String title;
    private String url;
    @JsonProperty(value = "isRequired")
    private boolean isRequired;
    @JsonProperty(value = "isAgree")
    private boolean isAgree;
}
