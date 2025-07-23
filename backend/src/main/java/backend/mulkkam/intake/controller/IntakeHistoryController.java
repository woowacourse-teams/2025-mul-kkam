package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.service.IntakeHistoryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<IntakeHistorySummaryResponse>> getDailyResponses(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);
        List<IntakeHistorySummaryResponse> dailyResponses = intakeHistoryService.getDailyResponses(dateRangeRequest,
                1L);
        return ResponseEntity.ok().body(dailyResponses);
    }

    @PostMapping
    public void create(@RequestBody IntakeHistoryCreateRequest intakeHistoryCreateRequest) {
        intakeHistoryService.create(intakeHistoryCreateRequest, 1L);
    }
}
