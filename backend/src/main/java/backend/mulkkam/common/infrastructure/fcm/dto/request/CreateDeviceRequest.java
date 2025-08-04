package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.infrastructure.fcm.domain.Device;
import backend.mulkkam.member.domain.Member;

public record CreateDeviceRequest(
        String token,
        String deviceId
) {

    public Device toDevice(Member member) {
        return new Device(token, deviceId, member);
    }
}
