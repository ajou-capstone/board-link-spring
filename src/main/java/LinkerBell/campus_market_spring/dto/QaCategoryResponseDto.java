package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QaCategory;

public class QaCategoryResponseDto {
    private final QaCategory[] categories;

    public QaCategoryResponseDto(QaCategory[] categories) {
        this.categories = categories;
    }
}
