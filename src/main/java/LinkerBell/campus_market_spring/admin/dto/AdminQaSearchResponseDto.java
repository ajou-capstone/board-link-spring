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
@AllArgsConstructor
@NoArgsConstructor
public class AdminQaSearchResponseDto {

    private Long qaId;
    private Long userId;
    private QaCategory category;
    private String title;
    @JsonProperty(value = "isCompleted") private Boolean isCompleted;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime createdDate;

    public AdminQaSearchResponseDto(QA qa) {
        this.qaId = qa.getQaId();
        this.userId = qa.getUser().getUserId();
        this.category = qa.getCategory();
        this.title = qa.getTitle();
        this.createdDate = qa.getCreatedDate();
        this.isCompleted = qa.isCompleted();
    }
}
