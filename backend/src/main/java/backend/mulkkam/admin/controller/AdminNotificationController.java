package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.request.SendAdminBroadcastNotificationRequest;
import backend.mulkkam.admin.dto.response.GetAdminNotificationListResponse;
import backend.mulkkam.admin.service.AdminNotificationService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 - 알림 관리", description = "어드민 알림 관리 API")
@RequireAuth(level = AuthLevel.ADMIN)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @Operation(summary = "알림 목록 조회", description = "전체 알림 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public Page<GetAdminNotificationListResponse> getNotifications(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminNotificationService.getNotifications(pageable);
    }

    @Operation(summary = "전체 회원 알림 전송", description = "모든 회원에게 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "알림 전송 성공")
    @PostMapping("/broadcast")
    public ResponseEntity<Void> sendBroadcastNotification(
            @Valid @RequestBody SendAdminBroadcastNotificationRequest request
    ) {
        adminNotificationService.sendBroadcastNotification(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId
    ) {
        adminNotificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
