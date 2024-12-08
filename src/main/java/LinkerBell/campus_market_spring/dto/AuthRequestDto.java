package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthRequestDto {

    @NotBlank
    private String idToken;
    @NotBlank
    private String firebaseToken;
}
