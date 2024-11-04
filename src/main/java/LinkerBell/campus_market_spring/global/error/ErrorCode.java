package LinkerBell.campus_market_spring.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, 4001, "Invalid Google idToken"),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, 4002, "Invalid JWT"),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, 4003, "JWT가 만료되었습니다."),
    LOGOUT_JWT(HttpStatus.UNAUTHORIZED, 4004, "Logout한 JWT"),
    UNVERIFIED_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, 4005, "Google idToken이 확인되지 않습니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.UNAUTHORIZED, 4006, "Google email is not verified"),
    JWT_IS_NULL(HttpStatus.UNAUTHORIZED, 4007, "jwt의 값이 null입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4011, "사용자가 존재하지 않습니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, 4012, "아이템이 존재하지 않습니다."),
    SCHOOL_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 4013, "학교 이메일이 등록되지 않았습니다."),
    CAMPUS_NOT_FOUND(HttpStatus.NOT_FOUND, 4014, "캠퍼스가 존재하지 않습니다."),
    INVALID_SCHOOL_EMAIL(HttpStatus.NOT_FOUND, 4015, "이메일에 해당하는 캠퍼스가 존재하지 않습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, 4016, "잘못된 카테고리 값입니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST, 4017, "잘못된 가격입니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, 4018, "잘못된 sorting 입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, 4019, "인증 코드가 일치하지 않습니다."),
    INVALID_ITEM_PHOTOS_COUNT(HttpStatus.BAD_REQUEST, 4020, "아이템 사진 개수가 5개를 초과했습니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST, 4021, "잘못된 제목입니다."),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, 4022, "잘못된 내용입니다."),
    INVALID_THUMBNAIL(HttpStatus.BAD_REQUEST, 4023, "잘못된 썸네일 입니다."),
    INVALID_ITEM_PHOTOS(HttpStatus.BAD_REQUEST, 4024, "잘못된 이미지 입니다."),
    DUPLICATE_ITEM_PHOTOS(HttpStatus.BAD_REQUEST, 4025, "중복된 이미지가 있습니다."),
    USER_TERMS_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 4026, "사용자 약관 동의 정보가 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "서버 내부 오류입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
