package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.response.AdminFriendRelationListResponse;
import backend.mulkkam.admin.service.AdminFriendRelationService;
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

@Tag(name = "어드민 - 친구 관계 관리", description = "어드민 친구 관계 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/friend-relations")
public class AdminFriendRelationController {

    private final AdminFriendRelationService adminFriendRelationService;

    @Operation(summary = "친구 관계 목록 조회", description = "전체 친구 관계 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping
    public Page<AdminFriendRelationListResponse> getFriendRelations(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminFriendRelationService.getFriendRelations(pageable);
    }

    @Operation(summary = "친구 관계 삭제", description = "특정 친구 관계를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @RequireAuth(level = AuthLevel.ADMIN)
    @DeleteMapping("/{friendRelationId}")
    public ResponseEntity<Void> deleteFriendRelation(
            @Parameter(hidden = true) MemberDetails memberDetails,
            @PathVariable Long friendRelationId
    ) {
        adminFriendRelationService.deleteFriendRelation(friendRelationId);
        return ResponseEntity.noContent().build();
    }
}
