package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;

public record NotificationSettingsResponse(
        boolean isNightNotificationAgreed,
        boolean isMarketingNotificationAgreed
) {

    public NotificationSettingsResponse(Member member) {
        this(member.isNightNotificationAgreed(), member.isMarketingNotificationAgreed());
    }
}
