package backend.mulkkam.intake.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.service.IntakeAmountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "음용량", description = "사용자 음용량 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/amount")
public class IntakeAmountController {

    private final IntakeAmountService intakeAmountService;

    @Operation(summary = "사용자 맞춤 권장 음용량 조회", description = "사용자의 신체 정보를 기반으로 계산된 권장 음용량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntakeRecommendedAmountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 신체 정보", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 신체 정보", summary = "신체 정보 형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @GetMapping("/recommended")
    public ResponseEntity<IntakeRecommendedAmountResponse> getRecommended(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(memberDetails);
        return ResponseEntity.ok(intakeRecommendedAmountResponse);
    }

    @Operation(summary = "신체 정보 기반 권장 목표량 계산", description = "입력된 신체 정보를 기반으로 권장 목표 음용량을 계산합니다.")
    @ApiResponse(responseCode = "200", description = "계산 성공", content = @Content(schema = @Schema(implementation = RecommendedIntakeAmountResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 신체 정보", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 신체 정보", summary = "신체 정보 형식 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @GetMapping("/target/recommended")
    public ResponseEntity<RecommendedIntakeAmountResponse> getRecommendedTargetAmount(
            @Parameter(description = "신체 정보 (체중, 활동량 등)")
            @ModelAttribute PhysicalAttributesRequest physicalAttributesRequest
    ) {
        RecommendedIntakeAmountResponse recommendedIntakeAmountResponse = intakeAmountService.getRecommendedTargetAmount(
                physicalAttributesRequest);
        return ResponseEntity.ok(recommendedIntakeAmountResponse);
    }

    @Operation(summary = "목표 음용량 수정", description = "사용자의 목표 음용량을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 목표량", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 목표량", summary = "amount 범위 오류", value = "{\"code\":\"INVALID_AMOUNT\"}")}))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "404", description = "금일 음용 기록 없음", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "금일 음용 기록 없음", summary = "IntakeHistory 미존재", value = "{\"code\":\"NOT_FOUND_INTAKE_HISTORY\"}")}))
    @PatchMapping("/target")
    public ResponseEntity<Void> modifyTarget(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        intakeAmountService.modifyTarget(memberDetails, intakeTargetAmountModifyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "목표 음용량 조회", description = "사용자가 설정한 목표 음용량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntakeTargetAmountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/target")
    public ResponseEntity<IntakeTargetAmountResponse> getTarget(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        IntakeTargetAmountResponse intakeTargetAmountResponse = intakeAmountService.getTarget(memberDetails);
        return ResponseEntity.ok(intakeTargetAmountResponse);
    }

    @Operation(summary = "금일 목표 음용량을 요청한 음용량으로 설정", description = "금일 목표 음용량을 요청한 음용량으로 수정합니다.")
    @ApiResponse(responseCode = "200", description = "설정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 목표량", summary = "amount 범위 오류", value = "{\"code\":\"INVALID_AMOUNT\"}")}))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "404", description = "금일 음용 기록을 찾을 수 없음", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "금일 음용 기록 없음", summary = "IntakeHistory 미존재", value = "{\"code\":\"NOT_FOUND_INTAKE_HISTORY\"}")}))
    @PatchMapping("/target/suggested")
    public ResponseEntity<Void> modifyTargetBySuggested(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest
    ) {
        intakeAmountService.modifyDailyTargetBySuggested(memberDetails, modifyIntakeTargetAmountByRecommendRequest);
        return ResponseEntity.ok().build();
    }
}
