package backend.mulkkam.device.repository;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceUuidAndMemberId(String deviceUuid, Long memberId);

    List<Device> findAllByMember(Member member);

    void deleteByMember(Member member);

    void deleteByMemberIdAndDeviceUuid(Long memberId, String deviceUuid);
}
