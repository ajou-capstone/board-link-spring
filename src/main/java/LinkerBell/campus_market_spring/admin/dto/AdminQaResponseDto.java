package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AdminQaResponseDto(
    Long qaId,
    Long userId,
    String title,
    String description,
    QaCategory category,
    @JsonProperty(value = "isCompleted") Boolean isCompleted
) {

    public AdminQaResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(),
            qa.getTitle(), qa.getDescription(),
            qa.getCategory(), qa.isCompleted());
    }
}
