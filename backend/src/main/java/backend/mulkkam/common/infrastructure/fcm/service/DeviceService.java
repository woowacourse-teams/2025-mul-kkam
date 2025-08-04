package backend.mulkkam.common.infrastructure.fcm.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.domain.Device;
import backend.mulkkam.common.infrastructure.fcm.dto.request.CreateDeviceRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.DeviceRepository;
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
    public void create(CreateDeviceRequest createDeviceRequest, Long memberId) {
        Member member = getMember(memberId);
        Device device = createDeviceRequest.toDevice(member);
        deviceRepository.save(device);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
