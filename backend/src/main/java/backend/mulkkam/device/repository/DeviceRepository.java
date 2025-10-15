package backend.mulkkam.device.repository;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceUuidAndMemberId(String deviceUuid, Long memberId);

    List<Device> findAllByMember(Member member);

    void deleteByMember(Member member);

    void deleteByMemberIdAndDeviceUuid(Long memberId, String deviceUuid);

    @Query("""
    SELECT d.token
    FROM Device d 
    WHERE d.member.id IN :memberIds
    """)
    List<String> findAllTokenByMemberIdIn(@Param("memberIds") List<Long> memberIds
    );
}
