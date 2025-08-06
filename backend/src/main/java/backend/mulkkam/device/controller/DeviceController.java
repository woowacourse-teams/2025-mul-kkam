package backend.mulkkam.device.controller;

import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.service.DeviceService;
import backend.mulkkam.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Void> register(
            Member member,
            @RequestBody RegisterDeviceRequest registerDeviceRequest) {
        deviceService.register(registerDeviceRequest, member);
        return ResponseEntity.ok().build();
    }
}
