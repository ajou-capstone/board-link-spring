package LinkerBell.campus_market_spring.global.error;

import LinkerBell.campus_market_spring.admin.dto.AdminUserReportRequestDto;
import LinkerBell.campus_market_spring.admin.dto.AdminQaRequestDto;
import LinkerBell.campus_market_spring.dto.AuthRequestDto;
import LinkerBell.campus_market_spring.dto.CampusRequestDto;
import LinkerBell.campus_market_spring.dto.MailRequestDto;
import LinkerBell.campus_market_spring.dto.ProfileRequestDto;
import LinkerBell.campus_market_spring.dto.QaRequestDto;
import LinkerBell.campus_market_spring.admin.dto.AdminItemReportRequestDto;
import LinkerBell.campus_market_spring.dto.ReviewRequestDto;
import LinkerBell.campus_market_spring.dto.VerificationCodeRequestDto;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(),
            errorCode.getCode());
        log.error("Error : {}", errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMisMatchException(
        MethodArgumentTypeMismatchException ex) {

        if (ex.getName().equals("minPrice") || ex.getName().equals("maxPrice")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PRICE);
            log.error(ErrorCode.INVALID_PRICE.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_PRICE.getHttpStatus());
        } else if (ex.getName().equals("category")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CATEGORY);
            log.error(ErrorCode.INVALID_CATEGORY.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_CATEGORY.getHttpStatus());
        } else if (ex.getName().equals("itemId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_ID);
            log.error(ErrorCode.INVALID_ITEM_ID.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_ITEM_ID.getHttpStatus());
        } else if (ex.getName().equals("page")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PAGEABLE_PAGE);
            log.error(ErrorCode.INVALID_PAGEABLE_PAGE.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_PAGEABLE_PAGE.getHttpStatus());
        } else if (ex.getName().equals("size")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PAGEABLE_SIZE);
            log.error(ErrorCode.INVALID_PAGEABLE_SIZE.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_PAGEABLE_SIZE.getHttpStatus());
        } else if (ex.getName().equals("notificationId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PAGEABLE_SIZE);
            log.error(ErrorCode.INVALID_PAGEABLE_SIZE.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_PAGEABLE_SIZE.getHttpStatus());
        } else if (ex.getName().equals("keywordId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_KEYWORD_ID);
            log.error(ErrorCode.INVALID_KEYWORD_ID.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_KEYWORD_ID.getHttpStatus());
        } else if (ex.getName().equals("itemStatus")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_STATUS);
            log.error(ErrorCode.INVALID_ITEM_STATUS.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_ITEM_STATUS.getHttpStatus());
        } else if (ex.getName().equals("isDeleted")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.REQUIRE_DELETE_OR_NOT);
            log.error(ErrorCode.REQUIRE_DELETE_OR_NOT.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.REQUIRE_DELETE_OR_NOT.getHttpStatus());
        } else if (ex.getName().equals("campusId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CAMPUS_NOT_FOUND);
            log.error(ErrorCode.CAMPUS_NOT_FOUND.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.CAMPUS_NOT_FOUND.getHttpStatus());
        } else if (ex.getName().equals("itemStatus")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_STATUS);
            log.error(ErrorCode.INVALID_ITEM_STATUS.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_ITEM_STATUS.getHttpStatus());
        }

        throw ex;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException ex)
        throws MethodArgumentNotValidException {

        String fieldName = Objects.requireNonNull(ex.getFieldError()).getField();

        if (fieldName.equals("description")) {
            if (ex.getBindingResult().getTarget() instanceof ReviewRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.INVALID_REVIEW_DESCRIPTION);
                log.error(ErrorCode.INVALID_REVIEW_DESCRIPTION.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_REVIEW_DESCRIPTION.getHttpStatus());
            }

            if (ex.getBindingResult().getTarget() instanceof QaRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.INVALID_QUESTION_DESCRIPTION);
                log.error(ErrorCode.INVALID_QUESTION_DESCRIPTION.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_QUESTION_DESCRIPTION.getHttpStatus());
            }
        } else if (fieldName.equals("rating")) {
            if (ex.getBindingResult().getTarget() instanceof ReviewRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_REVIEW_RATING);
                log.error(ErrorCode.INVALID_REVIEW_RATING.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_REVIEW_RATING.getHttpStatus());
            }
        } else if (fieldName.equals("title")) {
            if (ex.getBindingResult().getTarget() instanceof QaRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.INVALID_QUESTION_TITLE);
                log.error(ErrorCode.INVALID_QUESTION_TITLE.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_QUESTION_TITLE.getHttpStatus());
            }
        } else if (fieldName.equals("isDeleted")) {
            if (ex.getBindingResult().getTarget() instanceof AdminItemReportRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.REQUIRE_DELETE_OR_NOT);
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.REQUIRE_DELETE_OR_NOT.getHttpStatus());
            }
        } else if (fieldName.equals("isSuspended")) {
            if (ex.getBindingResult().getTarget() instanceof AdminUserReportRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.NOT_NULL_USER_REPORT_SUSPENDED_OR_NOT);
                log.error(ErrorCode.INVALID_QUESTION_TITLE.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.NOT_NULL_USER_REPORT_SUSPENDED_OR_NOT.getHttpStatus());
            }
        } else if (fieldName.equals("answerDescription")) {
            if (ex.getBindingResult().getTarget() instanceof AdminQaRequestDto) {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_QA_ANSWER);
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_QA_ANSWER.getHttpStatus());
            }
        } else if (fieldName.equals("email")) {
            if (ex.getBindingResult().getTarget() instanceof MailRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_SCHOOL_EMAIL);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_SCHOOL_EMAIL.getHttpStatus());
            }
        } else if (fieldName.equals("token")) {
            if (ex.getBindingResult().getTarget() instanceof VerificationCodeRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_VERIFICATION_TOKEN);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_SCHOOL_EMAIL.getHttpStatus());
            }
        } else if (fieldName.equals("verifyCode")) {
            if (ex.getBindingResult().getTarget() instanceof VerificationCodeRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_VERIFICATION_CODE);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_SCHOOL_EMAIL.getHttpStatus());
            }
        } else if (fieldName.equals("nickname")) {
            if (ex.getBindingResult().getTarget() instanceof ProfileRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_NICKNAME);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_NICKNAME.getHttpStatus());
            }
        } else if (fieldName.equals("profileImage")) {
            if (ex.getBindingResult().getTarget() instanceof ProfileRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_IMAGE_URL);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_IMAGE_URL.getHttpStatus());
            }
        } else if (fieldName.equals("campusId")) {
            if (ex.getBindingResult().getTarget() instanceof CampusRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.CAMPUS_ID_NOT_NULL);
                return new ResponseEntity<>(response,
                    ErrorCode.CAMPUS_ID_NOT_NULL.getHttpStatus());
            }
        } else if (fieldName.equals("idToken")) {
            if (ex.getBindingResult().getTarget() instanceof AuthRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_GOOGLE_TOKEN);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_GOOGLE_TOKEN.getHttpStatus());
            }
        } else if (fieldName.equals("firebaseToken")) {
            if (ex.getBindingResult().getTarget() instanceof AuthRequestDto) {
                ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_FIREBASE_TOKEN);
                return new ResponseEntity<>(response,
                    ErrorCode.INVALID_FIREBASE_TOKEN.getHttpStatus());
            }
        }

        if (Objects.requireNonNull(ex.getFieldError()).getField().equals("title")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TITLE);
            log.error(ErrorCode.INVALID_TITLE.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_TITLE.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("price")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PRICE);
            log.error(ErrorCode.INVALID_PRICE.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_PRICE.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("description")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_DESCRIPTION);
            log.error(ErrorCode.INVALID_DESCRIPTION.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_DESCRIPTION.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("thumbnail")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_THUMBNAIL);
            log.error(ErrorCode.INVALID_THUMBNAIL.getMessage());
            return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_THUMBNAIL.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().startsWith("images")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_PHOTOS);
            log.error(ErrorCode.INVALID_ITEM_PHOTOS.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_ITEM_PHOTOS.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("itemStatus")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_STATUS);
            log.error(ErrorCode.INVALID_ITEM_STATUS.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_ITEM_STATUS.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("buyerId")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_BUYER);
            log.error(ErrorCode.INVALID_ITEM_BUYER.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_ITEM_BUYER.getHttpStatus());
        } else if (Objects.requireNonNull(ex.getFieldError()).getField().equals("keywordName")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_KEYWORD_NAME);
            log.error(ErrorCode.INVALID_KEYWORD_NAME.getMessage());
            return new ResponseEntity<>(errorResponse,
                ErrorCode.INVALID_KEYWORD_NAME.getHttpStatus());
        }

        throw ex;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> notReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause() != null) {
            if (ex.getCause().getMessage()
                .contains("LinkerBell.campus_market_spring.domain.Category")) {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CATEGORY);
                log.error(ErrorCode.INVALID_CATEGORY.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_CATEGORY.getHttpStatus());
            } else if (ex.getCause().getMessage()
                .contains("LinkerBell.campus_market_spring.domain.ItemStatus")) {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ITEM_STATUS);
                log.error(ErrorCode.INVALID_ITEM_STATUS.getMessage());
                return new ResponseEntity<>(errorResponse,
                    ErrorCode.INVALID_ITEM_STATUS.getHttpStatus());
            }
        }
        throw ex;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex, HttpServletRequest request) {

        log.error("[{}], [{}], [{}], [{}]",
            ex.getMessage(), ex.getMethod().getName(), ex.getBody().getDetail(),
            request.getRequestURI());
        if (ex.getMethod().getName().equals("getPresignedPutUrl")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_FILE_NAME);
            return ResponseEntity.status(ErrorCode.INVALID_FILE_NAME.getHttpStatus())
                .body(errorResponse);
        }

        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException ex, HttpServletRequest request) {

        log.error("[{}], [{}], [{}], [{}]",
            ex.getMessage(), ex.getMethodParameter().getMethod().getName(), ex.getBody().getType(),
            request.getRequestURI());
        if (ex.getMethodParameter().getMethod().getName().equals("getPresignedPutUrl")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_FILE_NAME);
            return ResponseEntity.status(ErrorCode.INVALID_FILE_NAME.getHttpStatus())
                .body(errorResponse);
        }

        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(ex.getMessage()));
    }
}
