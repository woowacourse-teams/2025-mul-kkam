package backend.mulkkam.intake.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.intake.dto.CreateIntakeHistoryResponse;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.service.IntakeHistoryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "음수량 기록", description = "사용자 음수량 기록 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/history")
public class IntakeHistoryController {

    private final IntakeHistoryService intakeHistoryService;

    @Operation(summary = "음수량 기록 요약 조회", description = "지정된 기간 동안의 일별 음수량 기록 요약을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntakeHistorySummaryResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 날짜 범위", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 날짜 범위", summary = "from이 to보다 이후", value = "{\"code\":\"INVALID_DATE_RANGE\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping
    public ResponseEntity<List<IntakeHistorySummaryResponse>> readSummaryOfIntakeHistories(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2024-01-01")
            @RequestParam LocalDate from,
            @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2024-01-31")
            @RequestParam LocalDate to
    ) {
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);
        List<IntakeHistorySummaryResponse> dailyResponses = intakeHistoryService.readSummaryOfIntakeHistories(
                dateRangeRequest,
                memberDetails
        );
        return ResponseEntity.ok().body(dailyResponses);
    }

    @Operation(summary = "음수량 기록 생성", description = "새로운 음수량 기록을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "기록 생성 성공", content = @Content(schema = @Schema(implementation = CreateIntakeHistoryResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 요청", summary = "음수량 범위 오류 등", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "404", description = "컵을 찾을 수 없음", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "존재하지 않는 컵", summary = "cupId가 DB에 없음", value = "{\"code\":\"NOT_FOUND_CUP\"}")
    }))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "권한 없음", summary = "타인의 기록 접근", value = "{\"code\":\"NOT_PERMITTED_FOR_INTAKE_HISTORY\"}")
    }))
    @PostMapping
    public ResponseEntity<CreateIntakeHistoryResponse> create(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody IntakeDetailCreateRequest intakeDetailCreateRequest
    ) {
        CreateIntakeHistoryResponse createIntakeHistoryResponse = intakeHistoryService.create(
                intakeDetailCreateRequest,
                memberDetails
        );
        return ResponseEntity.ok(createIntakeHistoryResponse);
    }

    @Operation(summary = "음수량 기록 삭제", description = "특정 음수량 기록을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "기록 삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "404", description = "기록을 찾을 수 없음", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "기록 없음", summary = "id가 DB에 없음", value = "{\"code\":\"NOT_FOUND_INTAKE_HISTORY_DETAIL\"}")
    }))
    @ApiResponse(responseCode = "400", description = "금일 외 기록 삭제 불가", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "삭제 불가", summary = "오늘 기록만 삭제 가능", value = "{\"code\":\"INVALID_DATE_FOR_DELETE_INTAKE_HISTORY\"}")
    }))
    @DeleteMapping("/details/{id}") // TODO: POST 메서드와 endpoint 통일하기 ({id} 만 있어도 될듯?)
    public ResponseEntity<Void> deleteDetailHistory(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "삭제할 음수량 기록 id", required = true)
            @PathVariable Long id
    ) {
        intakeHistoryService.deleteDetailHistory(id, memberDetails);
        return ResponseEntity.ok().build();
    }
}
