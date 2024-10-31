package LinkerBell.campus_market_spring.dto;

import lombok.Builder;

@Builder
public class TermsResponseDto {
    private Long id;
    private String title;
    private String url;
    private boolean isRequired;
    private boolean isAgree;
}
