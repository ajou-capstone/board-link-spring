package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record QaResponseDto(
    Long qaId,
    Long userId,
    String title,
    String description,
    QaCategory category,
    @JsonProperty(value = "isCompleted") Boolean isCompleted,
    String answerDescription,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime answerDate,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdDate
) {

    public QaResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(), qa.getTitle(),
            qa.getDescription(), qa.getCategory(), qa.isCompleted(),
            qa.getAnswerDescription(), qa.getAnswerDate(), qa.getCreatedDate());
    }
}
