package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.service.CupService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController("/cups")
public class CupController {

    private final CupService cupService;

    public CupController(CupService cupService) {
        this.cupService = cupService;
    }

    @GetMapping()
    public void read() {

    }

    @PostMapping()
    public ResponseEntity<CupResponse> create(CupRegisterRequest cupRegisterRequest) {
        CupResponse cupResponse = cupService.create(cupRegisterRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cupResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(cupResponse);
    }
}
