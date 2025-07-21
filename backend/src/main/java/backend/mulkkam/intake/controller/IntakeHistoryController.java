package backend.mulkkam.intake.controller;

import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intake/history")
public class IntakeHistoryController {

    @GetMapping
    public void get(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {

    }

    @PostMapping
    public void create() {

    }
}
