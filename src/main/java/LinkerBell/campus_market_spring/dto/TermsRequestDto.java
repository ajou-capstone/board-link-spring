package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermsRequestDto {

    private Long termId;

    @JsonProperty(value = "isAgree")
    private boolean isAgree;
}
