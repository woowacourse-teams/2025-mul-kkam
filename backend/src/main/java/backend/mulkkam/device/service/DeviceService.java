package backend.mulkkam.device.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberAndDeviceUuidDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void register(
            RegisterDeviceRequest registerDeviceRequest,
            MemberAndDeviceUuidDetails memberAndDeviceUuidDetails
    ) {
        DevicePlatform platform = registerDeviceRequest.platform();
        deviceRepository.findByDeviceUuidAndMemberId(memberAndDeviceUuidDetails.deviceUuid(),
                        memberAndDeviceUuidDetails.id())
                .ifPresentOrElse((device) -> {
                    device.modifyToken(registerDeviceRequest.token());
                    device.modifyPlatform(platform);
                }, () -> {
                    Member member = getMember(memberAndDeviceUuidDetails.id());
                    Device device = registerDeviceRequest.toDevice(member, memberAndDeviceUuidDetails.deviceUuid(),
                            platform);
                    deviceRepository.save(device);
                });
    }

    @Transactional
    public void delete(MemberAndDeviceUuidDetails memberAndDeviceUuidDetails) {
        deviceRepository.deleteByMemberIdAndDeviceUuid(memberAndDeviceUuidDetails.id(),
                memberAndDeviceUuidDetails.deviceUuid());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }

}
