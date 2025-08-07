package backend.mulkkam.device.service;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public void register(RegisterDeviceRequest registerDeviceRequest, Member member) {
        Optional<Device> deviceOptional = deviceRepository.findByDeviceIdAndMemberId(
                registerDeviceRequest.deviceId(), member.getId());
        if (deviceOptional.isEmpty()) {
            Device device = registerDeviceRequest.toDevice(member);
            deviceRepository.save(device);
            return;
        }
        Device device = deviceOptional.get();
        device.modifyToken(registerDeviceRequest.token());
    }
}
