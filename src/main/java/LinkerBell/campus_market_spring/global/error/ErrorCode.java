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
    INVALID_TITLE(HttpStatus.BAD_REQUEST, 4021, "제목은 2글자 이상 50글자 이하여야 합니다."),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, 4022, "내용은 2글자 이상 1000자 이하여야 합니다."),
    INVALID_THUMBNAIL(HttpStatus.BAD_REQUEST, 4023, "잘못된 썸네일 입니다."),
    INVALID_ITEM_PHOTOS(HttpStatus.BAD_REQUEST, 4024, "잘못된 이미지 입니다."),
    DUPLICATE_ITEM_PHOTOS(HttpStatus.BAD_REQUEST, 4025, "중복된 이미지가 있습니다."),
    INVALID_ITEM_ID(HttpStatus.BAD_REQUEST, 4026, "잘못된 아이템 값입니다."),
    DELETED_ITEM_ID(HttpStatus.FORBIDDEN, 4027, "삭제된 아이템입니다."),
    NOT_MATCH_USER_CAMPUS_WITH_ITEM_CAMPUS(HttpStatus.FORBIDDEN, 4028, "아이템의 캠퍼스와 일치하지 않는 캠퍼스입니다."),
    USER_TERMS_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 4029, "사용자 약관 동의 정보가 없습니다."),
    DUPLICATE_SCHOOL_EMAIL(HttpStatus.BAD_REQUEST, 4030, "중복된 학교 이메일입니다."),
    NOT_MATCH_USER_ID_WITH_ITEM_USER_ID(HttpStatus.FORBIDDEN, 4031, "해당 아이템 게시글의 작성자가 아닙니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, 4032, "파일 이름을 비우거나 공백으로 할 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 4033, "채팅방이 존재하지 않습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, 4034, "채팅 메시지가 존재하지 않습니다."),
    INVALID_ITEM_STATUS(HttpStatus.BAD_REQUEST, 4035, "잘못된 아이템 거래 상태입니다."),
    INVALID_ITEM_BUYER(HttpStatus.BAD_REQUEST, 4036, "잘못된 아이템 구매자입니다."),
    DO_NOT_ROLL_BACK_ITEM_STATUS_FOR_SALE(HttpStatus.BAD_REQUEST, 4037, "아이템을 판매중인 상태로 바꿀 수 없습니다."),
    ALREADY_SOLD_OUT_ITEM(HttpStatus.BAD_REQUEST, 4038, "이미 거래 완료된 아이템 입니다."),
    NOT_MATCH_USER_CAMPUS(HttpStatus.FORBIDDEN, 4039, "캠퍼스가 일치하지 않는 사용자입니다."),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, 4040, "잘못된 메시지 conentType입니다."),
    DUPLICATE_CHATROOM(HttpStatus.BAD_REQUEST, 4041, "같은 판매자와 아이템을 가진 채팅방이 이미 있습니다."),
    EMPTY_ITEM_THUMBNAIL(HttpStatus.BAD_REQUEST, 4042, "빈 썸네일은 등록할 수 없습니다."),
    NOT_ADMINISTRATOR(HttpStatus.FORBIDDEN, 4043, "관리자 권한이 없는 사용자입니다."),
    NOT_REPORT_OWN(HttpStatus.BAD_REQUEST, 4044, "자기 자신을 신고할 수 없습니다."),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, 4045, "해당 요청에 권한이 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "서버 내부 오류입니다."),

    FCM_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, 6001, "유효하지 않은 FCM 토큰입니다."),
    FCM_UNREGISTERED(HttpStatus.NOT_FOUND, 6002, "만료된 FCM 토큰입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
