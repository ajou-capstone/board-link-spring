package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VerificationCodeRequestDto {

    @NotNull
    private String token;

    @NotNull
    private String verifyCode;
}
