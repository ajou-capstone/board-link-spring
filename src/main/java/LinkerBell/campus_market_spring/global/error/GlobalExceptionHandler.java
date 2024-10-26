package LinkerBell.campus_market_spring.global.error;

import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        log.info("Error : " + errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

}
