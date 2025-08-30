package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "제안 알림", description = "사용자 제안 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/suggestion-notifications")
public class SuggestionNotificationController {

    private final SuggestionNotificationService suggestionNotificationService;

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
