package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.service.ReminderScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리마인드 알림 설정", description = "사용자 리마인드 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/reminder")
public class ReminderScheduleController {

    private final ReminderScheduleService reminderScheduleService;

    @Operation(summary = "사용자 리마인더 스케쥴링 생성", description = "사용자의 리마인더 스케쥴링을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "알림 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필드 형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping()
    public ResponseEntity<Void> create(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody CreateReminderScheduleRequest createReminderScheduleRequest
    ) {
        reminderScheduleService.create(memberDetails, createReminderScheduleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 리마인더 스케쥴링 목록 조회", description = "사용자의 리마인더 스케쥴링을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadReminderSchedulesResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "필수 파라미터 누락/형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping()
    public ResponseEntity<ReadReminderSchedulesResponse> read(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return ResponseEntity.ok().body(reminderScheduleService.read(memberDetails));
    }

    @Operation(summary = "사용자 리마인더 스케쥴링 시간 수정", description = "사용자의 특정 리마인더 스케쥴의 시간을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping()
    public ResponseEntity<Void> modifyTime(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody ModifyReminderScheduleTimeRequest modifyReminderScheduleTimeRequest
    ) {
        reminderScheduleService.modifyTime(memberDetails, modifyReminderScheduleTimeRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 리마인더 스케쥴링 삭제", description = "사용자의 특정 리마인더 스케쥴링을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 id")
    @ApiResponse(responseCode = "401", description = "삭제할 권한이 없는 사용자의 요청")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @PathVariable Long id
    ) {
        reminderScheduleService.delete(memberDetails, id);
        return ResponseEntity.noContent().build();
    }
}
