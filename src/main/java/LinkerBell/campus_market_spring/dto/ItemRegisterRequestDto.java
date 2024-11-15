package LinkerBell.campus_market_spring.dto;

import LinkerBell.campus_market_spring.domain.Category;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRegisterRequestDto {

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    @NotBlank
    @Size(min = 2, max = 1000)
    private String description;

    @NotNull
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer price;

    private Category category;

    @Pattern(regexp = "^$|^(|https?|ftp):\\/\\/([\\w\\-]+\\.)+[\\w\\-]+(\\/[-a-zA-Z0-9@:%._\\+~#=]*)*$",
        message = "Must be a valid URL."
    )
    private String thumbnail;

    private List<@Pattern(
        regexp = "^$|^(https?|ftp):\\/\\/([\\w\\-]+\\.)+[\\w\\-]+(\\/[-a-zA-Z0-9@:%._\\+~#=]*)*$",
        message = "Must be a valid URL."
    ) String> images;

}
