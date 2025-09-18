package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "제안 알림", description = "사용자 제안 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/suggestion-notifications")
public class SuggestionNotificationController {

    private final SuggestionNotificationService suggestionNotificationService;

    @Operation(summary = "다음 release 버전 활동량 알림 생성", description = "사용자의 활동량에 따라 알림을 생성합니다. 다음 release 시 사용 예정")
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

    @Operation(summary = "권장 목표량 적용", description = "제안 알림(ID)을 승인하여 회원의 일일 목표 음용량을 적용합니다.")
    @ApiResponse(responseCode = "200", description = "적용 성공", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "리소스 없음",
            content = @Content(
                    schema = @Schema(implementation = FailureBody.class),
                    examples = {
                            @ExampleObject(name = "제안 알림 없음", value = "{\"code\":\"NOT_FOUND_SUGGESTION_NOTIFICATION\"}"),
                            @ExampleObject(name = "회원 없음", value = "{\"code\":\"NOT_FOUND_MEMBER\"}")
                    }
            )
    )
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping("/approval/{id}")
    public ResponseEntity<Void> applyTargetAmount(
            @PathVariable @Parameter(description = "제안 알림 ID") Long id,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        suggestionNotificationService.applyTargetAmount(id, memberDetails);
        return ResponseEntity.ok().build();
    }
}
