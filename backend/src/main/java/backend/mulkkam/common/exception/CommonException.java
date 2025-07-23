package backend.mulkkam.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException {

    private final HttpStatus status;

    public CommonException(String name, HttpStatus status) {
        super(name);
        this.status = status;
    }
}
