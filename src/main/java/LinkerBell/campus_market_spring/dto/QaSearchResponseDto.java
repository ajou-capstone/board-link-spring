package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record QaSearchResponseDto(
    Long qaId,
    Long userId,
    QaCategory category,
    String title,
    @JsonProperty(value = "isCompleted") Boolean isCompleted,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime answerDate,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdDate
) {

    public QaSearchResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(),
            qa.getCategory(), qa.getTitle(), qa.isCompleted(),
            qa.getAnswerDate(), qa.getCreatedDate());
    }
}
