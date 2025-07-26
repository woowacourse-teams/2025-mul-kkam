package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.IntakeAmountResponse;
import backend.mulkkam.intake.dto.IntakeTargetAmountModifyRequest;
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
    public ResponseEntity<IntakeAmountResponse> getRecommended() {
        IntakeAmountResponse intakeAmountResponse = intakeAmountService.getRecommended(1L);
        return ResponseEntity.ok(intakeAmountResponse);
    }

    @PatchMapping("/target")
    public ResponseEntity<Void> modifyTarget(
            @RequestBody IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest) {
        intakeAmountService.modifyTarget(intakeTargetAmountModifyRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
