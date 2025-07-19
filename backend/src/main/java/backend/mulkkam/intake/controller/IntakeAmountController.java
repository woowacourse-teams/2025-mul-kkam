package backend.mulkkam.intake.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/intake-amount")
public class IntakeAmountController {

    @GetMapping
    public void get(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {

    }

    @GetMapping("/recommended")
    public void getRecommended() {

    }

    @PutMapping("/target")
    public void updateTarget() {

    }

    @PostMapping
    public void create() {

    }
}
