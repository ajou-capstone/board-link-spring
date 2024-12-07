package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QaSearchResponseDto {

    private Long qaId;
    private Long userId;
    private QaCategory category;
    private String title;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime answerDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime createdDate;

    public QaSearchResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(),
            qa.getCategory(), qa.getTitle(), qa.isCompleted(),
            qa.getAnswerDate(), qa.getCreatedDate());
    }
}
