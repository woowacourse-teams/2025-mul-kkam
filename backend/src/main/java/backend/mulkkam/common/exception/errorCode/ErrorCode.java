package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String name();

    HttpStatus getStatus();
}
