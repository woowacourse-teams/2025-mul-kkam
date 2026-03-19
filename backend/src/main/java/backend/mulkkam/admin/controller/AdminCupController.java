package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.request.UpdateAdminCupRequest;
import backend.mulkkam.admin.dto.response.GetAdminCupDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminCupListResponse;
import backend.mulkkam.admin.service.AdminCupService;
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

@Tag(name = "어드민 - 컵 관리", description = "어드민 컵 관리 API")
@RequireAuth(level = AuthLevel.ADMIN)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/cups")
public class AdminCupController {

    private final AdminCupService adminCupService;

    @Operation(summary = "컵 목록 조회", description = "전체 컵 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public Page<GetAdminCupListResponse> getCups(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminCupService.getCups(pageable);
    }

    @Operation(summary = "컵 상세 조회", description = "특정 컵의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{cupId}")
    public GetAdminCupDetailResponse getCup(
            @PathVariable Long cupId
    ) {
        return adminCupService.getCup(cupId);
    }

    @Operation(summary = "컵 수정", description = "특정 컵 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "수정 성공")
    @PutMapping("/{cupId}")
    public ResponseEntity<Void> updateCup(
            @PathVariable Long cupId,
            @Valid @RequestBody UpdateAdminCupRequest request
    ) {
        adminCupService.updateCup(cupId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "컵 삭제", description = "특정 컵을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{cupId}")
    public ResponseEntity<Void> deleteCup(
            @PathVariable Long cupId
    ) {
        adminCupService.deleteCup(cupId);
        return ResponseEntity.noContent().build();
    }
}
