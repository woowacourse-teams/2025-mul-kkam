package backend.mulkkam.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "어드민 SQL 쿼리 실행 결과")
public record ExecuteAdminSqlResponse(
        @Schema(description = "쿼리 타입 (SELECT, INSERT, UPDATE, DELETE)")
        String queryType,

        @Schema(description = "SELECT 쿼리 결과 (SELECT인 경우만)")
        List<Map<String, Object>> rows,

        @Schema(description = "영향받은 행 수 (INSERT, UPDATE, DELETE인 경우)")
        Integer affectedRows,

        @Schema(description = "실행 시간 (ms)")
        long executionTimeMs,

        @Schema(description = "성공 여부")
        boolean success,

        @Schema(description = "에러 메시지 (실패 시)")
        String errorMessage
) {
    public static ExecuteAdminSqlResponse selectSuccess(
            List<Map<String, Object>> rows,
            long executionTimeMs
    ) {
        return new ExecuteAdminSqlResponse("SELECT", rows, null, executionTimeMs, true, null);
    }

    public static ExecuteAdminSqlResponse modifySuccess(
            String queryType,
            int affectedRows,
            long executionTimeMs
    ) {
        return new ExecuteAdminSqlResponse(queryType, null, affectedRows, executionTimeMs, true, null);
    }

    public static ExecuteAdminSqlResponse error(String errorMessage) {
        return new ExecuteAdminSqlResponse(null, null, null, 0, false, errorMessage);
    }
}
