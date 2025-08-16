package backend.mulkkam.common.exception.errorCode;

import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INTER_SERVER_ERROR_CODE;

import backend.mulkkam.common.exception.CommonException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum FirebaseErrorCode implements ErrorCode {

    THIRD_PARTY_AUTH_ERROR(HttpStatus.UNAUTHORIZED),

    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),

    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR),

    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS),

    SENDER_ID_MISMATCH(HttpStatus.FORBIDDEN),

    UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),

    UNREGISTERED(HttpStatus.GONE);

    private final HttpStatus httpStatus;

    FirebaseErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static FirebaseErrorCode findByName(String name) {
        return Arrays.stream(FirebaseErrorCode.values())
                .filter(firebaseErrorCode -> firebaseErrorCode.name().equals(name))
                .findAny()
                .orElseThrow(() -> new CommonException(INTER_SERVER_ERROR_CODE, name));
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }
}
