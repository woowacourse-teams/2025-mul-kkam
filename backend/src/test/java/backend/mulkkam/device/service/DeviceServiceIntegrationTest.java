package backend.mulkkam.device.service;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_DEVICE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_DEVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.DeviceFixtureBuilder;
import backend.mulkkam.support.fixture.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeviceServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DeviceService deviceService;

    @DisplayName("기기의 FCM 토큰을 삭제할 때")
    @Nested
    class DeleteFcmToken {

        @DisplayName("Device Id 가 존재하는 경우 정상적으로 삭제된다")
        @Test
        void success_whenDeviceIdIsExisted() {
            // given
            String deviceId = "deviceId";

            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            Device device = DeviceFixtureBuilder
                    .withMember(member)
                    .deviceId(deviceId)
                    .build();
            deviceRepository.save(device);

            MemberDetails memberDetails = new MemberDetails(member);

            // when
            deviceService.deleteFcmToken(deviceId, memberDetails);

            // then
            Device updatedDevice = deviceRepository.findById(device.getId()).orElse(null);
            assertThat(updatedDevice.getToken()).isNull();
        }

        @DisplayName("Device Id 가 존재하지 않는 경우 예외가 발생한다")
        @Test
        void error_whenDeviceIdIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            MemberDetails memberDetails = new MemberDetails(member);

            // when & then
            assertThatThrownBy(() -> deviceService.deleteFcmToken("invalidId", memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(NOT_FOUND_DEVICE.name());
        }

        @DisplayName("Device Id 가 존재하지 않는 경우 예외가 발생한다")
        @Test
        void error_whenDeviceIdIsNotOwnedByMember() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            String deviceId = "deviceId";
            Device device = DeviceFixtureBuilder
                    .withMember(member)
                    .deviceId(deviceId)
                    .build();
            deviceRepository.save(device);

            Member anotherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            memberRepository.save(anotherMember);

            MemberDetails memberDetails = new MemberDetails(anotherMember);

            // when & then
            assertThatThrownBy(() -> deviceService.deleteFcmToken(deviceId, memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(NOT_PERMITTED_FOR_DEVICE.name());
        }
    }
}
