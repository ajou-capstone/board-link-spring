package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.TermsRequestDto;
import java.util.List;
import lombok.Getter;

@Getter
public class TermsCollectionRequestDto {

    private List<TermsRequestDto> terms;
}
