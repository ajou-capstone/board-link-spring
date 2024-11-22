package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QaCategory;

public record QaRequestDto(String title,
                           QaCategory category,
                           String description) {

}
