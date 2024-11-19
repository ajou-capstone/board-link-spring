package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
public class AuthUserDto {

    private Long userId;
    private String loginEmail;

    @Builder
    public AuthUserDto(Long userId, String loginEmail) {
        this.userId = userId;
        this.loginEmail = loginEmail;
    }

}
