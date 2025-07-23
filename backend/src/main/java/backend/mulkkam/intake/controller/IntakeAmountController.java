package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.IntakeAmountUpdateRequest;
import backend.mulkkam.intake.service.IntakeAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/amount")
public class IntakeAmountController {

    private final IntakeAmountService intakeAmountService;

    @GetMapping("/recommended")
    public void getRecommended() {

    }

    @PutMapping("/target")
    public ResponseEntity<Void> updateTarget(IntakeAmountUpdateRequest intakeAmountUpdateRequest) {
        intakeAmountService.updateTarget(intakeAmountUpdateRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
