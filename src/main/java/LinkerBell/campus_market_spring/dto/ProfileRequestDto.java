package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequestDto {

    @NotBlank
    @Size(min=2, max=10)
    private String nickname;
    
    @Pattern(regexp = "^$|^(|https?|ftp):\\/\\/([\\w\\-]+\\.)+[\\w\\-]+(\\/[-a-zA-Z0-9@:%._\\+~#=]*)*$",
        message = "Must be a valid URL."
    )
    private String profileImage;
}
