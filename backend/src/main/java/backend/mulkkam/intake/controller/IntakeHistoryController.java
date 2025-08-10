package backend.mulkkam.intake.controller;

import backend.mulkkam.intake.dto.CreateIntakeHistoryResponse;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.service.IntakeHistoryService;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            Member member,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        DateRangeRequest dateRangeRequest = new DateRangeRequest(from, to);
        List<IntakeHistorySummaryResponse> dailyResponses = intakeHistoryService.readSummaryOfIntakeHistories(
                dateRangeRequest,
                member);
        return ResponseEntity.ok().body(dailyResponses);
    }

    @PostMapping
    public ResponseEntity<CreateIntakeHistoryResponse> create(
            Member member,
            @RequestBody IntakeDetailCreateRequest intakeDetailCreateRequest) {
        CreateIntakeHistoryResponse createIntakeHistoryResponse = intakeHistoryService.create(intakeDetailCreateRequest,
                member);
        return ResponseEntity.ok(createIntakeHistoryResponse);
    }

    @DeleteMapping("/details/{id}")
    public ResponseEntity<Void> deleteDetailHistory(
            Member member,
            @PathVariable Long id
    ) {
        intakeHistoryService.deleteDetailHistory(id, member);
        return ResponseEntity.ok().build();
    }
}
