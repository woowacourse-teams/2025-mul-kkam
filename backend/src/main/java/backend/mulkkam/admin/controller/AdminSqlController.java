package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.request.ExecuteAdminSqlRequest;
import backend.mulkkam.admin.dto.response.ExecuteAdminSqlResponse;
import backend.mulkkam.admin.service.AdminSqlService;
import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import backend.mulkkam.common.dto.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 - SQL 실행", description = "어드민 SQL 직접 실행 API (주의: 강력한 권한)")
@RequireAuth(level = AuthLevel.ADMIN)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/sql")
public class AdminSqlController {

    private final AdminSqlService adminSqlService;

    @Operation(
            summary = "SQL 쿼리 실행",
            description = """
                    SQL 쿼리를 직접 실행합니다. 도메인 정책을 우회하여 데이터를 조회/삽입/수정/삭제할 수 있습니다.
                    
                    **주의사항:**
                    - SELECT, INSERT, UPDATE, DELETE만 지원
                    - DROP, TRUNCATE, ALTER 등 위험한 명령어는 차단됨
                    - 모든 쿼리 실행은 로그로 기록됨
                    """
    )
    @ApiResponse(responseCode = "200", description = "쿼리 실행 완료 (성공/실패 여부는 응답 내용 확인)")
    @PostMapping("/execute")
    public ExecuteAdminSqlResponse executeSql(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @Valid @RequestBody ExecuteAdminSqlRequest request
    ) {
        return adminSqlService.executeSql(request, memberDetails.id());
    }
}
