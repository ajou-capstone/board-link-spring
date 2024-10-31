package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TermsResponseDto {
    private Long id;
    private String title;
    private String url;
    private boolean isRequired;
    private boolean isAgree;
}
