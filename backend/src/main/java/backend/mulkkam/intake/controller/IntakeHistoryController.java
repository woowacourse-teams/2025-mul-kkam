package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.service.IntakeHistoryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/intake/history")
public class IntakeHistoryController {

    private final IntakeHistoryService intakeHistoryService;

    @GetMapping
    public void get(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {

    }

    @PatchMapping
    public ResponseEntity<Void> create(@RequestBody IntakeHistoryCreateRequest intakeHistoryCreateRequest) {
        intakeHistoryService.create(intakeHistoryCreateRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
