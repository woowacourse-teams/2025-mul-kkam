package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.request.UpdateAdminIntakeHistoryRequest;
import backend.mulkkam.admin.dto.response.GetAdminIntakeHistoryDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminIntakeHistoryListResponse;
import backend.mulkkam.admin.service.AdminIntakeHistoryService;
import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 - 섭취 기록 관리", description = "어드민 섭취 기록 관리 API")
@RequireAuth(level = AuthLevel.ADMIN)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/intake-histories")
public class AdminIntakeHistoryController {

    private final AdminIntakeHistoryService adminIntakeHistoryService;

    @Operation(summary = "섭취 기록 목록 조회", description = "전체 섭취 기록 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public Page<GetAdminIntakeHistoryListResponse> getIntakeHistories(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminIntakeHistoryService.getIntakeHistories(pageable);
    }

    @Operation(summary = "섭취 기록 상세 조회", description = "특정 섭취 기록의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{intakeHistoryId}")
    public GetAdminIntakeHistoryDetailResponse getIntakeHistory(
            @PathVariable Long intakeHistoryId
    ) {
        return adminIntakeHistoryService.getIntakeHistory(intakeHistoryId);
    }

    @Operation(summary = "섭취 기록 수정", description = "특정 섭취 기록의 목표량을 수정합니다.")
    @ApiResponse(responseCode = "204", description = "수정 성공")
    @PutMapping("/{intakeHistoryId}")
    public ResponseEntity<Void> updateIntakeHistory(
            @PathVariable Long intakeHistoryId,
            @Valid @RequestBody UpdateAdminIntakeHistoryRequest request
    ) {
        adminIntakeHistoryService.updateIntakeHistory(intakeHistoryId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "섭취 기록 삭제", description = "특정 섭취 기록을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{intakeHistoryId}")
    public ResponseEntity<Void> deleteIntakeHistory(
            @PathVariable Long intakeHistoryId
    ) {
        adminIntakeHistoryService.deleteIntakeHistory(intakeHistoryId);
        return ResponseEntity.noContent().build();
    }
}
