package backend.mulkkam.common.infrastructure.fcm.repository;

import backend.mulkkam.common.infrastructure.fcm.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
