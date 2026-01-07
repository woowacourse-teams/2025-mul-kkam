package backend.mulkkam.device.controller;

import backend.mulkkam.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer Worker에서 호출하는 내부 API
 * Compensating Action을 위한 엔드포인트 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/devices")
@Profile("!consumer") // API Server에서만 활성화
public class InternalDeviceController {

    private final DeviceService deviceService;

    @PostMapping("/invalid-token")
    public ResponseEntity<Void> deleteInvalidToken(
            @RequestBody InvalidTokenRequest request
    ) {
        log.info("[INTERNAL API] Delete invalid token request: memberId={}",
                request.memberId());

        deviceService.deleteByMemberIdAndToken(request.memberId(), request.token());

        return ResponseEntity.ok().build();
    }

    public record InvalidTokenRequest(Long memberId, String token) {
    }
}
