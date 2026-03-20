package backend.mulkkam.device.dto;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "디바이스 등록 요청")
public record RegisterDeviceRequest(
        @Schema(description = "FCM 토큰 - 디바이스 하나 당 하나의 토큰만 가질 수 있음")
        String token,
        @NotNull
        @Schema(description = "디바이스 플랫폼", example = "ANDROID")
        DevicePlatform platform
) {

    public Device toDevice(
            Member member,
            String deviceUuid,
            DevicePlatform platform
    ) {
        return new Device(token, deviceUuid, member, platform);
    }
}
