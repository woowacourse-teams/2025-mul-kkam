package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.service.IntakeAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/amount")
public class IntakeAmountController {

    private final IntakeAmountService intakeAmountService;

    @GetMapping("/recommended")
    public ResponseEntity<IntakeRecommendedAmountResponse> getRecommended() {
        IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(1L);
        return ResponseEntity.ok(intakeRecommendedAmountResponse);
    }

    @GetMapping("/target/recommended")
    public ResponseEntity<RecommendedIntakeAmountResponse> getRecommendedTargetAmount(
            @RequestBody PhysicalAttributesRequest physicalAttributesRequest
    ) {
        RecommendedIntakeAmountResponse recommendedIntakeAmountResponse = intakeAmountService.getRecommendedTargetAmount(
                physicalAttributesRequest);
        return ResponseEntity.ok(recommendedIntakeAmountResponse);
    }

    @PatchMapping("/target")
    public ResponseEntity<Void> modifyTarget(
            @RequestBody IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest) {
        intakeAmountService.modifyTarget(intakeTargetAmountModifyRequest, 1L);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/target")
    public ResponseEntity<IntakeTargetAmountResponse> getTarget() {
        IntakeTargetAmountResponse intakeTargetAmountResponse = intakeAmountService.getTarget(1L);
        return ResponseEntity.ok(intakeTargetAmountResponse);
    }
}
