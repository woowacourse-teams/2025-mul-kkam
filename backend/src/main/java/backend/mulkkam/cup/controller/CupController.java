package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.service.CupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/cups")
@RestController
public class CupController {

    private final CupService cupService;

    @GetMapping
    public void read() {

    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CupRegisterRequest cupRegisterRequest) {
        cupService.create(cupRegisterRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
