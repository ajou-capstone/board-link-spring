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
public class QaResponseDto {

    private Long qaId;
    private Long userId;
    private String title;
    private String description;
    private QaCategory category;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;
    private String answerDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime answerDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime createdDate;

    public QaResponseDto(QA qa) {
        this(qa.getQaId(), qa.getUser().getUserId(), qa.getTitle(),
            qa.getDescription(), qa.getCategory(), qa.isCompleted(),
            qa.getAnswerDescription(), qa.getAnswerDate(), qa.getCreatedDate());
    }
}
