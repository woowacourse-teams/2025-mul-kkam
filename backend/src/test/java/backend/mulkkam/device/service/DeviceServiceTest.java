package backend.mulkkam.device.service;

import static org.assertj.core.api.Assertions.assertThat;

import backend.mulkkam.common.dto.MemberAndDeviceUuidDetails;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.DeviceFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeviceServiceTest extends ServiceTest {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DeviceService deviceService;

    private MemberAndDeviceUuidDetails createMemberAndDeviceUuid() {
        String deviceUuid = "deviceId";

        Member member = MemberFixtureBuilder.builder().build();
        memberRepository.save(member);

        Device device = DeviceFixtureBuilder
                .withMember(member)
                .deviceUuid(deviceUuid)
                .build();
        deviceRepository.save(device);

        return new MemberAndDeviceUuidDetails(member, deviceUuid);
    }

    @DisplayName("기기의 FCM 토큰을 삭제할 때")
    @Nested
    class DeleteFcmToken {

        @DisplayName("Device Id 가 존재하는 경우 정상적으로 삭제된다")
        @Test
        void success_fcm_token_is_deleted() {
            // given
            MemberAndDeviceUuidDetails memberAndDeviceUuidDetails = createMemberAndDeviceUuid();

            // when
            deviceService.delete(memberAndDeviceUuidDetails);
            Optional<Device> device = deviceRepository.findByDeviceUuidAndMemberId(
                    memberAndDeviceUuidDetails.deviceUuid(),
                    memberAndDeviceUuidDetails.id());

            // then
            assertThat(device).isEmpty();
        }
    }
}
