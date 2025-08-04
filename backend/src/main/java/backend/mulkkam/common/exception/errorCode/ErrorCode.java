package backend.mulkkam.common.exception.errorCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 bad request
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
    INVALID_CUP_AMOUNT(HttpStatus.BAD_REQUEST, "컵 용량이 올바르지 않습니다."),
    INVALID_CUP_NICKNAME(HttpStatus.BAD_REQUEST, "컵 닉네임이 올바르지 않습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "날짜 범위가 올바르지 않습니다."),
    SAME_AS_BEFORE_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임이 변경되지 않았습니다."),
    INVALID_CUP_COUNT(HttpStatus.BAD_REQUEST, "컵 개수가 올바르지 않습니다."),
    INVALID_CUP_RANK_VALUE(HttpStatus.BAD_REQUEST, "컵 우선순위가 올바르지 않습니다."),
    INVALID_MEMBER_NICKNAME(HttpStatus.BAD_REQUEST, "회원 닉네임 형식이 올바르지 않습니다."),

    // 403 forbidden
    NOT_PERMITTED_FOR_CUP(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404 not found
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_FOUND_CUP(HttpStatus.NOT_FOUND, "존재하지 않는 컵입니다."),
    NOT_FOUND_INTAKE_TYPE(HttpStatus.NOT_FOUND, "존재하지 않는 컵 유형입니다."),

    // 409 conflict
    DUPLICATE_MEMBER_NICKNAME(HttpStatus.CONFLICT, "중복되는 닉네임이 존재합니다."),
    DUPLICATED_CUP(HttpStatus.CONFLICT, "중복되는 컵이 존재합니다."),
    DUPLICATED_CUP_RANKS(HttpStatus.CONFLICT, "중복되는 컵 우선순위가 존재합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String description;

    ErrorCode(HttpStatus httpStatus, String description) {
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
