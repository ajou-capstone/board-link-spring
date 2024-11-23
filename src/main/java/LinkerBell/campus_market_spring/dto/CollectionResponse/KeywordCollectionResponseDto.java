package LinkerBell.campus_market_spring.dto.CollectionResponse;

import LinkerBell.campus_market_spring.dto.KeywordResponseDto;
import java.util.List;

public record KeywordCollectionResponseDto(List<KeywordResponseDto> keywordList) {

    public static KeywordCollectionResponseDto from(List<KeywordResponseDto> keywordList) {
        return new KeywordCollectionResponseDto(keywordList);
    }
}
