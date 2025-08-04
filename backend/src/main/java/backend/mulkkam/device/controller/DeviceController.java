package backend.mulkkam.device.controller;

import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.service.DeviceService;
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

    @PostMapping()
    public ResponseEntity<Void> register(@RequestBody RegisterDeviceRequest registerDeviceRequest) {
        deviceService.register(registerDeviceRequest, 1L);
        return ResponseEntity.ok().build();
    }
}
