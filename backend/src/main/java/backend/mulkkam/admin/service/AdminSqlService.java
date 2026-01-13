package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.request.ExecuteAdminSqlRequest;
import backend.mulkkam.admin.dto.response.ExecuteAdminSqlResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminSqlService {

    private final JdbcTemplate jdbcTemplate;

    private static final Set<String> BLOCKED_COMMANDS = Set.of(
            "DROP", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE",
            "RENAME", "REPLACE", "LOAD", "CALL", "EXECUTE"
    );

    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);

    public ExecuteAdminSqlResponse executeSql(
            ExecuteAdminSqlRequest request,
            Long adminMemberId
    ) {
        String sql = request.sql().trim();

        String normalizedSql = sql.replaceAll("--.*?\n", " ")
                .replaceAll("/\\*.*?\\*/", " ")
                .replaceAll("\\s+", " ")
                .trim();
        String upperSql = normalizedSql.toUpperCase();

        for (String blocked : BLOCKED_COMMANDS) {
            if (upperSql.contains(blocked)) {
                log.warn("[Admin SQL] BLOCKED - adminId: {}, sql: {}", adminMemberId, sql);
                return ExecuteAdminSqlResponse.error("차단된 명령어입니다: " + blocked);
            }
        }
        log.info("[Admin SQL] EXECUTE - adminId: {}, sql: {}", adminMemberId, sql);

        long startTime = System.currentTimeMillis();
        int TIMEOUT_SEC = 10;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ExecuteAdminSqlResponse> future = executor.submit(() -> {
            try {
                if (SELECT_PATTERN.matcher(normalizedSql).find()) {
                    return executeSelect(sql, startTime);
                } else if (INSERT_PATTERN.matcher(normalizedSql).find()) {
                    return executeModify(sql, "INSERT", startTime);
                } else if (UPDATE_PATTERN.matcher(normalizedSql).find()) {
                    return executeModify(sql, "UPDATE", startTime);
                } else if (DELETE_PATTERN.matcher(normalizedSql).find()) {
                    return executeModify(sql, "DELETE", startTime);
                } else {
                    return ExecuteAdminSqlResponse.error("지원하지 않는 쿼리 타입입니다. SELECT, INSERT, UPDATE, DELETE만 가능합니다.");
                }
            } catch (Exception e) {
                log.error("[Admin SQL] ERROR - adminId: {}, sql: {}, error: {}", adminMemberId, sql, e.getMessage());
                return ExecuteAdminSqlResponse.error(e.getMessage());
            }
        });
        try {
            ExecuteAdminSqlResponse result = future.get(TIMEOUT_SEC, TimeUnit.SECONDS);
            log.info("[Admin SQL] SUCCESS - adminId: {}, sql: {}", adminMemberId, sql);
            return result;
        } catch (TimeoutException te) {
            future.cancel(true);
            log.error("[Admin SQL] TIMEOUT - adminId: {}, sql: {}", adminMemberId, sql);
            return ExecuteAdminSqlResponse.error("쿼리 실행 시간이 초과되었습니다(" + TIMEOUT_SEC + "초)");
        } catch (Exception e) {
            log.error("[Admin SQL] ERROR - adminId: {}, sql: {}, error: {}", adminMemberId, sql, e.getMessage());
            return ExecuteAdminSqlResponse.error(e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private ExecuteAdminSqlResponse executeSelect(
            String sql,
            long startTime
    ) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        long executionTime = System.currentTimeMillis() - startTime;
        return ExecuteAdminSqlResponse.selectSuccess(rows, executionTime);
    }

    private ExecuteAdminSqlResponse executeModify(
            String sql,
            String queryType,
            long startTime
    ) {
        int affectedRows = jdbcTemplate.update(sql);
        long executionTime = System.currentTimeMillis() - startTime;
        return ExecuteAdminSqlResponse.modifySuccess(queryType, affectedRows, executionTime);
    }
}
