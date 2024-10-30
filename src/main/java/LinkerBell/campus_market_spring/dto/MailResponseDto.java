package LinkerBell.campus_market_spring.dto;

import lombok.Getter;

@Getter
public class MailResponseDto {

    private String token;

    public MailResponseDto() {}

    public MailResponseDto(String token) {
        this.token = token;
    }
}
