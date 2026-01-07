package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.response.AdminIntakeHistoryDetailResponse;
import backend.mulkkam.admin.dto.response.AdminIntakeHistoryListResponse;
import backend.mulkkam.admin.service.AdminIntakeHistoryService;
import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import backend.mulkkam.common.dto.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "어드민 - 섭취 기록 관리", description = "어드민 섭취 기록 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/intake-histories")
public class AdminIntakeHistoryController {

    private final AdminIntakeHistoryService adminIntakeHistoryService;

    @Operation(summary = "섭취 기록 목록 조회", description = "전체 섭취 기록 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping
    public Page<AdminIntakeHistoryListResponse> getIntakeHistories(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminIntakeHistoryService.getIntakeHistories(pageable);
    }

    @Operation(summary = "섭취 기록 상세 조회", description = "특정 섭취 기록의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping("/{intakeHistoryId}")
    public AdminIntakeHistoryDetailResponse getIntakeHistory(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @PathVariable Long intakeHistoryId
    ) {
        return adminIntakeHistoryService.getIntakeHistory(intakeHistoryId);
    }

    @Operation(summary = "섭취 기록 삭제", description = "특정 섭취 기록을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @DeleteMapping("/{intakeHistoryId}")
    public ResponseEntity<Void> deleteIntakeHistory(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @PathVariable Long intakeHistoryId
    ) {
        adminIntakeHistoryService.deleteIntakeHistory(intakeHistoryId);
        return ResponseEntity.noContent().build();
    }
}
