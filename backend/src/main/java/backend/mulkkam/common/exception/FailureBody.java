package backend.mulkkam.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "에러 응답 바디")
@Getter
@AllArgsConstructor
public class FailureBody {

    @Schema(description = "에러 코드")
    private String code;
}
