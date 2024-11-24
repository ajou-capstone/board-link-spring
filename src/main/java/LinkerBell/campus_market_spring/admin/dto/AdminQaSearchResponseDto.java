package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record AdminQaSearchResponseDto(
    Long qaId,
    Long userId,
    QaCategory category,
    String title,
    @JsonProperty(value = "isCompleted") Boolean isComplete,
    LocalDateTime createdDate
) {

    public AdminQaSearchResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(),
            qa.getCategory(), qa.getTitle(), qa.isCompleted(),
            qa.getCreatedDate());
    }
}
