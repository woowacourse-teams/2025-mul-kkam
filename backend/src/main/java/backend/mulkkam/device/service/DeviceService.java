package backend.mulkkam.device.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
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
            MemberDetails memberDetails
    ) {
        deviceRepository.findByDeviceUuidAndMemberId(registerDeviceRequest.deviceUuid(), memberDetails.id())
                .ifPresentOrElse((device) -> {
                    device.modifyToken(registerDeviceRequest.token());
                }, () -> {
                    Member member = getMember(memberDetails.id());
                    Device device = registerDeviceRequest.toDevice(member);
                    deviceRepository.save(device);
                });
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
