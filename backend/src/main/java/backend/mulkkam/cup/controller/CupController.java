package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.service.CupService;
import backend.mulkkam.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/cups")
@RestController
public class CupController {

    private final CupService cupService;

    @GetMapping
    public ResponseEntity<CupsResponse> readAllSorted(Member member) {
        return ResponseEntity.ok(cupService.readSortedCupsByMemberId(member));
    }

    @PostMapping
    public ResponseEntity<Void> create(
            Member member,
            @RequestBody CreateCupRequest registerCupRequest
    ) {
        cupService.create(
                registerCupRequest,
                member
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ranks")
    public ResponseEntity<CupsRanksResponse> updateRanks(
            Member member,
            @RequestBody UpdateCupRanksRequest request
    ) {
        CupsRanksResponse response = cupService.updateRanks(request, member);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cupId}")
    public ResponseEntity<Void> modifyNicknameAndAmount(
            Member member,
            @RequestBody UpdateCupRequest updateCupRequest,
            @PathVariable Long cupId
    ) {
        cupService.update(
                cupId,
                member,
                updateCupRequest
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Member member,
            @PathVariable("id") Long id
    ) {
        cupService.delete(id, member);
        return ResponseEntity.noContent().build();
    }
}
