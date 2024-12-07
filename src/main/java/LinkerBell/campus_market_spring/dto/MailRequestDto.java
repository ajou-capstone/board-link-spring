package LinkerBell.campus_market_spring.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MailRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = "올바른 이메일 형식을 사용해야합니다.")
    private String email;
}
