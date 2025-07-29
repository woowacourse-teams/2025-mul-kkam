package backend.mulkkam.member.controller;

import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.service.MemberService;
import java.time.LocalDate;
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
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/physical-attributes")
    public ResponseEntity<Void> modifyPhysicalAttributes(
            @RequestBody PhysicalAttributesModifyRequest physicalAttributesModifyRequest) {
        memberService.modifyPhysicalAttributes(
                physicalAttributesModifyRequest,
                1L
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members/progress-info")
    public ResponseEntity<ProgressInfoResponse> getProgressInfo(@RequestParam LocalDate date) {
        DateRangeRequest dateRangeRequest = new DateRangeRequest(date, date);
        return ResponseEntity.ok(memberService.getTodayProgressInfo(dateRangeRequest, 1L));
    }
}
