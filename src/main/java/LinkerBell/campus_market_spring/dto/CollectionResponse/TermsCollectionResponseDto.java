package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import java.util.List;

public record TermsCollectionResponseDto (List<TermsResponseDto> terms) {
    public static  TermsCollectionResponseDto from(List<TermsResponseDto> terms) {
        return new TermsCollectionResponseDto(terms);
    }
}
