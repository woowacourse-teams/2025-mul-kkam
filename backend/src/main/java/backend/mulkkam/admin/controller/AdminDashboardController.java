package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.response.GetAdminDashboardStatsResponse;
import backend.mulkkam.admin.service.AdminDashboardService;
import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import backend.mulkkam.common.dto.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 - 대시보드", description = "어드민 대시보드 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(summary = "대시보드 통계 조회", description = "대시보드에 표시할 각종 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping("/stats")
    public GetAdminDashboardStatsResponse getDashboardStats(
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        return adminDashboardService.getDashboardStats();
    }
}
