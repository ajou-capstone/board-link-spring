package LinkerBell.campus_market_spring.global.error;

import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<ErrorResponse> typeMisMatchException(
        MethodArgumentTypeMismatchException ex) {

        if (ex.getName().equals("minPrice") || ex.getName().equals("maxPrice")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PRICE);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getName().equals("category")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CATEGORY);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getName().equals("itemId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_ID);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        throw ex;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException ex)
        throws MethodArgumentNotValidException {

        if (ex.getFieldError().getField().equals("title")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TITLE);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getFieldError().getField().equals("price")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PRICE);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getFieldError().getField().equals("description")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_DESCRIPTION);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getFieldError().getField().equals("thumbnail")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_THUMBNAIL);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        } else if (ex.getFieldError().getField().startsWith("images")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_PHOTOS);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        throw ex;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> notReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause().getMessage()
            .contains("LinkerBell.campus_market_spring.domain.Category")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CATEGORY);
            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        throw ex;
    }
}
