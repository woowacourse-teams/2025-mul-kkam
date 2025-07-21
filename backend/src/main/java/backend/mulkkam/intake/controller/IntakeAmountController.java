package backend.mulkkam.intake.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intake/amount")
public class IntakeAmountController {

    @GetMapping("/recommended")
    public void getRecommended() {

    }

    @PutMapping("/target")
    public void updateTarget() {

    }
}
