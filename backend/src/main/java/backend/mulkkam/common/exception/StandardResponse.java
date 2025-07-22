package backend.mulkkam.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StandardResponse<U> extends ResponseEntity<U> {

    private StandardResponse(
            U body,
            HttpStatus status
    ) {
        super(body, status);
    }

    public static StandardResponse<FailureBody> failure(
            String code,
            HttpStatus status
    ) {
        return new StandardResponse<>(
                new FailureBody(code),
                status
        );
    }

    @Getter
    @AllArgsConstructor
    public static class FailureBody {
        private String code;
    }
}
