package backend.mulkkam.device.dto;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;

public record RegisterDeviceRequest(
        String token,
        String deviceId
) {

    public Device toDevice(Member member) {
        return new Device(token, deviceId, member);
    }
}
