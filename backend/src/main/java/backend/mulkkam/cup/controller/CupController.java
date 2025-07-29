package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.service.CupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Void> create(@RequestBody CupRegisterRequest cupRegisterRequest) {
        cupService.create(cupRegisterRequest, 1L);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ranks")
    public ResponseEntity<CupsRanksResponse> updateRanks(@RequestBody UpdateCupRanksRequest request) {
        CupsRanksResponse response = cupService.updateRanks(request, 1L);
        return ResponseEntity.ok(response);
    }
}
