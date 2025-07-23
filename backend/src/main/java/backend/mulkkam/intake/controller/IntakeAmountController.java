package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.IntakeAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeAmountResponse;
import backend.mulkkam.intake.service.IntakeAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/target")
    public ResponseEntity<Void> modifyTarget(@RequestBody IntakeAmountModifyRequest intakeAmountModifyRequest) {
        intakeAmountService.modifyTarget(intakeAmountModifyRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
