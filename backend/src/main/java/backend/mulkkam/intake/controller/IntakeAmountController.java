package backend.mulkkam.intake.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/intake")
public class IntakeAmountController {

    @GetMapping("/history")
    public void get(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {

    }

    @GetMapping("/amount/recommended")
    public void getRecommended() {

    }

    @PutMapping("/amount/target")
    public void updateTarget() {

    }

    @PostMapping("/history")
    public void create() {

    }
}
