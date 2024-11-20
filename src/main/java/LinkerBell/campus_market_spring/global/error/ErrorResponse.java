package LinkerBell.campus_market_spring.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ErrorResponse(String message) {
        this.message = message;
        this.code = 6666;
    }
}
