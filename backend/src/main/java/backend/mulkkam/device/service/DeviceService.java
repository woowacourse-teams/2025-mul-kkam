package backend.mulkkam.device.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.Optional;
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
    public void register(RegisterDeviceRequest registerDeviceRequest, Long memberId) {
        Member member = getMember(memberId);
        Optional<Device> deviceOptional = deviceRepository.findByDeviceIdAndMemberId(
                registerDeviceRequest.deviceId(), member.getId());
        if (deviceOptional.isEmpty()) {
            Device device = registerDeviceRequest.toDevice(member);
            deviceRepository.save(device);
            return ;
        }
        Device device = deviceOptional.get();
        device.modifyToken(registerDeviceRequest.token());
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
