package backend.mulkkam.intake.controller;

import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

@Tag(name = "음수량", description = "사용자 음수량 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/amount")
public class IntakeAmountController {

    private final IntakeAmountService intakeAmountService;

    @Operation(summary = "사용자 맞춤 권장 음수량 조회", description = "사용자의 신체 정보를 기반으로 계산된 권장 음수량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntakeRecommendedAmountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/recommended")
    public ResponseEntity<IntakeRecommendedAmountResponse> getRecommended(
            @Parameter(hidden = true)
            Member member
    ) {
        IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(member);
        return ResponseEntity.ok(intakeRecommendedAmountResponse);
    }

    @Operation(summary = "신체 정보 기반 권장 목표량 계산", description = "입력된 신체 정보를 기반으로 권장 목표 음수량을 계산합니다.")
    @ApiResponse(responseCode = "200", description = "계산 성공", content = @Content(schema = @Schema(implementation = RecommendedIntakeAmountResponse.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 신체 정보", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/target/recommended")
    public ResponseEntity<RecommendedIntakeAmountResponse> getRecommendedTargetAmount(
            @Parameter(description = "신체 정보 (체중, 활동량 등)")
            @ModelAttribute PhysicalAttributesRequest physicalAttributesRequest
    ) {
        RecommendedIntakeAmountResponse recommendedIntakeAmountResponse = intakeAmountService.getRecommendedTargetAmount(
                physicalAttributesRequest);
        return ResponseEntity.ok(recommendedIntakeAmountResponse);
    }

    @Operation(summary = "목표 음수량 수정", description = "사용자의 목표 음수량을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 목표량", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PatchMapping("/target")
    public ResponseEntity<Void> modifyTarget(
            @Parameter(hidden = true)
            Member member,
            @RequestBody IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        intakeAmountService.modifyTarget(member, intakeTargetAmountModifyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "목표 음수량 조회", description = "사용자가 설정한 목표 음수량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntakeTargetAmountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/target")
    public ResponseEntity<IntakeTargetAmountResponse> getTarget(
            @Parameter(hidden = true)
            Member member
    ) {
        IntakeTargetAmountResponse intakeTargetAmountResponse = intakeAmountService.getTarget(member);
        return ResponseEntity.ok(intakeTargetAmountResponse);
    }

    @Operation(summary = "금일 목표 음수량을 제안된 음수량으로 설정", description = "날씨 및 운동 정보로 계산된 권장 음수량을 금일 목표 음수량으로 설정합니다.")
    @ApiResponse(responseCode = "200", description = "설정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PatchMapping("/target/suggested")
    public ResponseEntity<Void> modifyTargetBySuggested(
            @Parameter(hidden = true)
            Member member,
            @RequestBody ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest
    ) {
        intakeAmountService.modifyDailyTargetBySuggested(member, modifyIntakeTargetAmountByRecommendRequest);
        return ResponseEntity.ok().build();
    }
}
