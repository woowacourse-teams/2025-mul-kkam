package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.service.ActivityService;
import backend.mulkkam.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "사용자 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final ActivityService activityService;
    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "특정 시점 이후의 알림 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadNotificationsResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필수 파라미터 누락/형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping
    ResponseEntity<ReadNotificationsResponse> getNotifications(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "알림 조회 조건")
            @Valid @ModelAttribute GetNotificationsRequest getNotificationsRequest
    ) {
        ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                getNotificationsRequest, memberDetails
        );
        return ResponseEntity.ok(readNotificationsResponse);
    }

    @Operation(summary = "활동량 알림 생성", description = "사용자의 활동량에 따라 알림을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "알림 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필드 형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping("/activity")
    ResponseEntity<Void> createNotificationByActivity(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody CreateActivityNotification createActivityNotification
    ) {
        activityService.createActivityNotification(createActivityNotification, memberDetails);
        return ResponseEntity.ok().build();
    }
}
