package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.AdminDeviceListResponse;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminDeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public Page<AdminDeviceListResponse> getDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable)
                .map(AdminDeviceListResponse::from);
    }

    @Transactional
    public void deleteDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
        deviceRepository.delete(device);
    }
}
