package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.service.CupService;
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
    public ResponseEntity<CupsResponse> read() {
        return ResponseEntity.ok().body(cupService.readCupsByMemberId(1L));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateCupRequest registerCupRequest) {
        cupService.create(
                registerCupRequest,
                1L
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ranks")
    public ResponseEntity<CupsRanksResponse> updateRanks(@RequestBody UpdateCupRanksRequest request) {
        CupsRanksResponse response = cupService.updateRanks(request, 1L);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cupId}")
    public ResponseEntity<Void> modifyNicknameAndAmount(
            @RequestBody UpdateCupRequest updateCupRequest,
            @PathVariable Long cupId
    ) {
        cupService.update(
                cupId,
                1L,
                updateCupRequest
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        cupService.delete(id, 1L);
        return ResponseEntity.noContent().build();
    }
}
