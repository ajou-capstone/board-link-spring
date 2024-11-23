package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRegisterRequestDto {

    @NotBlank
    @Size(min = 1, max = 25)
    private String keywordName;
}
