package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<IntakeRecommendedAmountResponse> getRecommended(Member member) {
        IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(member);
        return ResponseEntity.ok(intakeRecommendedAmountResponse);
    }

    @GetMapping("/target/recommended")
    public ResponseEntity<RecommendedIntakeAmountResponse> getRecommendedTargetAmount(
            @ModelAttribute PhysicalAttributesRequest physicalAttributesRequest
    ) {
        RecommendedIntakeAmountResponse recommendedIntakeAmountResponse = intakeAmountService.getRecommendedTargetAmount(
                physicalAttributesRequest);
        return ResponseEntity.ok(recommendedIntakeAmountResponse);
    }

    @PatchMapping("/target")
    public ResponseEntity<Void> modifyTarget(
            Member member,
            @RequestBody IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest
    ) {
        intakeAmountService.modifyTarget(member, intakeTargetAmountModifyRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/target")
    public ResponseEntity<IntakeTargetAmountResponse> getTarget(Member member) {
        IntakeTargetAmountResponse intakeTargetAmountResponse = intakeAmountService.getTarget(member);
        return ResponseEntity.ok(intakeTargetAmountResponse);
    }

    @PatchMapping("/target/suggested")
    public ResponseEntity<Void> modifyTargetBySuggested(
            Member member,
            @RequestBody ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest
    ) {
        intakeAmountService.modifyDailyTargetBySuggested(member, modifyIntakeTargetAmountByRecommendRequest);
        return ResponseEntity.ok().build();
    }
}
