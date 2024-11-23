package LinkerBell.campus_market_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordResponseDto {

    private Long keywordId;
    private String keywordName;
}