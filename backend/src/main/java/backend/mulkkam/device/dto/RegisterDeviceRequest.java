package backend.mulkkam.device.dto;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "디바이스 등록 요청")
public record RegisterDeviceRequest(
        @Schema(description = "FCM 토큰 - 디바이스 하나 당 하나의 토큰만 가질 수 있음")
        String token
) {

    public Device toDevice(
            Member member,
            String deviceUuid
    ) {
        return new Device(token, deviceUuid, member);
    }
}
