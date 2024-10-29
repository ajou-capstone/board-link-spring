package LinkerBell.campus_market_spring.global.error;

import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        log.info("Error : " + errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMisMatchException(MethodArgumentTypeMismatchException ex) {

        if (ex.getName().equals("minPrice") || ex.getName().equals("maxPrice")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PRICE);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if(ex.getName().equals("category")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CATEGORY);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        throw ex;
    }

}
