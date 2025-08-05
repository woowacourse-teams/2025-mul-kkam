package backend.mulkkam.device.repository;

import backend.mulkkam.device.domain.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceIdAndMemberId(String deviceId, Long memberId);
}
