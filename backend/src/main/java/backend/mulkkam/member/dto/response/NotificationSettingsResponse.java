package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 알림 수신 여부 응답")
public record NotificationSettingsResponse(
        @Schema(description = "야간 알림", example = "true")
        boolean isNightNotificationAgreed,

        @Schema(description = "마케팅 알림", example = "true")
        boolean isMarketingNotificationAgreed
) {

    public NotificationSettingsResponse(Member member) {
        this(member.isNightNotificationAgreed(), member.isMarketingNotificationAgreed());
    }
}
