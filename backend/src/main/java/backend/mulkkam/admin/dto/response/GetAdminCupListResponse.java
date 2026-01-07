package backend.mulkkam.admin.dto.response;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import java.time.LocalDateTime;

public record GetAdminCupListResponse(
        Long id,
        Long memberId,
        String memberNickname,
        String nickname,
        Integer cupAmount,
        Integer cupRank,
        IntakeType intakeType,
        Long cupEmojiId,
        LocalDateTime createdAt
) {
    public static GetAdminCupListResponse from(Cup cup) {
        return new GetAdminCupListResponse(
                cup.getId(),
                cup.getMember().getId(),
                cup.getMember().getMemberNickname() != null ? cup.getMember().getMemberNickname().value() : null,
                cup.getNickname() != null ? cup.getNickname().value() : null,
                cup.getCupAmount() != null ? cup.getCupAmount().value() : null,
                cup.getCupRank() != null ? cup.getCupRank().value() : null,
                cup.getIntakeType(),
                cup.getCupEmoji() != null ? cup.getCupEmoji().getId() : null,
                cup.getCreatedAt()
        );
    }
}
