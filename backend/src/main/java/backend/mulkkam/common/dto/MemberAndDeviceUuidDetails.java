package backend.mulkkam.common.dto;

import backend.mulkkam.member.domain.Member;

public record MemberAndDeviceUuidDetails(
        Long id,
        String deviceUuid
) {
    public MemberAndDeviceUuidDetails(Member member, String deviceUuid) {
        this(member.getId(), deviceUuid);
    }
}
