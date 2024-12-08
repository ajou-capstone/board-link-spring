package LinkerBell.campus_market_spring.admin.dto;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminQaResponseDto {

    private Long qaId;
    private Long userId;
    private String title;
    private String description;
    private QaCategory category;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;
    private String answerDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime answerDate;

    public AdminQaResponseDto(QA qa) {
        this.qaId = qa.getQaId();
        this.userId = qa.getUser().getUserId();
        this.title = qa.getTitle();
        this.description = qa.getDescription();
        this.category = qa.getCategory();
        this.isCompleted = qa.isCompleted();

        if (this.isCompleted) {
            this.answerDescription = qa.getAnswerDescription();
            this.answerDate = qa.getAnswerDate();
        }
    }
}
