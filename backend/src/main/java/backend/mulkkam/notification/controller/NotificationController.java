package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.notification.dto.request.CreateActivityNotification;
import backend.mulkkam.notification.dto.request.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.response.GetUnreadNotificationsCountResponse;
import backend.mulkkam.notification.dto.response.ReadNotificationsResponse;
import backend.mulkkam.notification.service.NotificationService;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "사용자 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SuggestionNotificationService suggestionNotificationService;

    @Operation(summary = "알림 목록 조회", description = "특정 시점 이후의 알림 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadNotificationsResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필수 파라미터 누락/형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping
    public ResponseEntity<ReadNotificationsResponse> readNotifications(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "알림 조회 조건")
            @Valid @ModelAttribute ReadNotificationsRequest readNotificationsRequest
    ) {
        ReadNotificationsResponse readNotificationsResponse = notificationService.readNotificationsAfter(
                readNotificationsRequest, memberDetails
        );
        return ResponseEntity.ok(readNotificationsResponse);
    }

    @Operation(summary = "읽지 않은 알림 수 조회", description = "사용자의 읽지 않은 알림 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GetUnreadNotificationsCountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/unread-count")
    public ResponseEntity<GetUnreadNotificationsCountResponse> getUnreadNotificationsCount(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestParam(required = false) // TODO: 다음 릴리스에 안드에게 추가 필드로 안내
            LocalDateTime clientTime
    ) {
        if (clientTime == null) {
            clientTime = LocalDateTime.now();
        }
        GetUnreadNotificationsCountResponse getUnreadNotificationsCountResponse = notificationService.getUnReadNotificationsCount(
                memberDetails, clientTime);
        return ResponseEntity.ok(getUnreadNotificationsCountResponse);
    }

    @Deprecated
    @Operation(summary = "현재 release 버전(2.0.0) 활동량 알림 생성", description = "사용자의 활동량에 따라 알림을 생성합니다. 다음 release 시 제거 예정")
    @ApiResponse(responseCode = "200", description = "알림 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필드 형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping("/activity")
    public ResponseEntity<Void> createNotificationByActivity(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody CreateActivityNotification createActivityNotification
    ) {
        suggestionNotificationService.createActivityNotification(createActivityNotification, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 삭제", description = "사용자의 알림을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 id")
    @ApiResponse(responseCode = "401", description = "삭제할 권한이 없는 사용자의 요청")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        notificationService.delete(memberDetails, id);
        return ResponseEntity.noContent().build();
    }
}
