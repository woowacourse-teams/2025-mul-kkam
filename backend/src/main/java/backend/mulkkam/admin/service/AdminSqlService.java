package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.request.ExecuteAdminSqlRequest;
import backend.mulkkam.admin.dto.response.ExecuteAdminSqlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminSqlService {

    private final JdbcTemplate jdbcTemplate;

    // 위험한 SQL 명령어 차단
    private static final Set<String> BLOCKED_COMMANDS = Set.of(
            "DROP", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE",
            "RENAME", "REPLACE", "LOAD", "CALL", "EXECUTE"
    );

    // SQL 타입 판별용 패턴
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);

    public ExecuteAdminSqlResponse executeSql(ExecuteAdminSqlRequest request, Long adminMemberId) {
        String sql = request.sql().trim();

        // 위험한 명령어 체크
        String upperSql = sql.toUpperCase();
        for (String blocked : BLOCKED_COMMANDS) {
            if (upperSql.contains(blocked)) {
                log.warn("[Admin SQL] BLOCKED - adminId: {}, sql: {}", adminMemberId, sql);
                return ExecuteAdminSqlResponse.error("차단된 명령어입니다: " + blocked);
            }
        }

        // 로그 기록
        log.info("[Admin SQL] EXECUTE - adminId: {}, sql: {}", adminMemberId, sql);

        long startTime = System.currentTimeMillis();

        try {
            if (SELECT_PATTERN.matcher(sql).find()) {
                return executeSelect(sql, startTime);
            } else if (INSERT_PATTERN.matcher(sql).find()) {
                return executeModify(sql, "INSERT", startTime);
            } else if (UPDATE_PATTERN.matcher(sql).find()) {
                return executeModify(sql, "UPDATE", startTime);
            } else if (DELETE_PATTERN.matcher(sql).find()) {
                return executeModify(sql, "DELETE", startTime);
            } else {
                return ExecuteAdminSqlResponse.error("지원하지 않는 쿼리 타입입니다. SELECT, INSERT, UPDATE, DELETE만 가능합니다.");
            }
        } catch (Exception e) {
            log.error("[Admin SQL] ERROR - adminId: {}, sql: {}, error: {}", adminMemberId, sql, e.getMessage());
            return ExecuteAdminSqlResponse.error(e.getMessage());
        }
    }

    private ExecuteAdminSqlResponse executeSelect(String sql, long startTime) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        long executionTime = System.currentTimeMillis() - startTime;
        return ExecuteAdminSqlResponse.selectSuccess(rows, executionTime);
    }

    private ExecuteAdminSqlResponse executeModify(String sql, String queryType, long startTime) {
        int affectedRows = jdbcTemplate.update(sql);
        long executionTime = System.currentTimeMillis() - startTime;
        return ExecuteAdminSqlResponse.modifySuccess(queryType, affectedRows, executionTime);
    }
}
