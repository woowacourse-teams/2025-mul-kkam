package backend.mulkkam.admin.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_DEVICE;

import backend.mulkkam.admin.dto.response.GetAdminDeviceListResponse;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminDeviceService {

    private final DeviceRepository deviceRepository;

    public Page<GetAdminDeviceListResponse> getDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable)
                .map(GetAdminDeviceListResponse::from);
    }

    @Transactional
    public void deleteDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_DEVICE));
        deviceRepository.delete(device);
    }
}
