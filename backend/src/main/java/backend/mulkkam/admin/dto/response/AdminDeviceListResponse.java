package backend.mulkkam.admin.dto.response;

import backend.mulkkam.device.domain.Device;
import java.time.LocalDateTime;

public record AdminDeviceListResponse(
        Long id,
        Long memberId,
        String memberNickname,
        String deviceUuid,
        String token,
        LocalDateTime createdAt
) {
    public static AdminDeviceListResponse from(Device device) {
        return new AdminDeviceListResponse(
                device.getId(),
                device.getMember().getId(),
                device.getMember().getMemberNickname() != null ? device.getMember().getMemberNickname().value() : null,
                device.getDeviceUuid(),
                device.getToken() != null ? device.getToken().substring(0, Math.min(20, device.getToken().length())) + "..." : null,
                device.getCreatedAt()
        );
    }
}
