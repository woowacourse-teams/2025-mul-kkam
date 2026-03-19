package backend.mulkkam.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "어드민 SQL 쿼리 실행 요청")
public record ExecuteAdminSqlRequest(
        @Schema(description = "실행할 SQL 쿼리", example = "SELECT * FROM member LIMIT 10")
        @NotBlank(message = "SQL 쿼리는 필수입니다.")
        String sql
) {
}
