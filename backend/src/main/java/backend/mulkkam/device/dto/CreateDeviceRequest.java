package backend.mulkkam.device.dto;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;

public record CreateDeviceRequest(
        String token,
        String deviceId
) {

    public Device toDevice(Member member) {
        return new Device(token, deviceId, member);
    }
}
