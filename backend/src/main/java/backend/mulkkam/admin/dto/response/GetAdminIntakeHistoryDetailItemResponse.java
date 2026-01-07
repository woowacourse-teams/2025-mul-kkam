package backend.mulkkam.admin.dto.response;

import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import java.time.LocalTime;

public record GetAdminIntakeHistoryDetailItemResponse(
        Long id,
        LocalTime intakeTime,
        IntakeType intakeType,
        Integer intakeAmount,
        String cupEmojiUrl
) {
    public static GetAdminIntakeHistoryDetailItemResponse from(IntakeHistoryDetail detail) {
        return new GetAdminIntakeHistoryDetailItemResponse(
                detail.getId(),
                detail.getIntakeTime(),
                detail.getIntakeType(),
                detail.getIntakeAmount() != null ? detail.getIntakeAmount().value() : null,
                detail.getCupEmojiUrl() != null ? detail.getCupEmojiUrl().value() : null
        );
    }
}
