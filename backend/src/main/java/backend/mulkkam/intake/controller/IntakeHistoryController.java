package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.service.IntakeHistoryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<IntakeHistorySummaryResponse>> readSummaryOfIntakeHistories(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);
        List<IntakeHistorySummaryResponse> dailyResponses = intakeHistoryService.readSummaryOfIntakeHistories(
                dateRangeRequest,
                1L);
        return ResponseEntity.ok().body(dailyResponses);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody IntakeHistoryCreateRequest intakeHistoryCreateRequest) {
        intakeHistoryService.create(intakeHistoryCreateRequest, 1L);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Long id) {
        intakeHistoryService.delete(id, 1L);
        return ResponseEntity.ok().build();
    }
}
