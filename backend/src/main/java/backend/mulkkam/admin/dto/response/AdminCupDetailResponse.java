package backend.mulkkam.admin.dto.response;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import java.time.LocalDateTime;

public record AdminCupDetailResponse(
        Long id,
        Long memberId,
        String memberNickname,
        String nickname,
        Integer cupAmount,
        Integer cupRank,
        IntakeType intakeType,
        Long cupEmojiId,
        String cupEmojiCode,
        LocalDateTime createdAt
) {
    public static AdminCupDetailResponse from(Cup cup) {
        return new AdminCupDetailResponse(
                cup.getId(),
                cup.getMember().getId(),
                cup.getMember().getMemberNickname() != null ? cup.getMember().getMemberNickname().value() : null,
                cup.getNickname() != null ? cup.getNickname().value() : null,
                cup.getCupAmount() != null ? cup.getCupAmount().value() : null,
                cup.getCupRank() != null ? cup.getCupRank().value() : null,
                cup.getIntakeType(),
                cup.getCupEmoji() != null ? cup.getCupEmoji().getId() : null,
                cup.getCupEmoji() != null && cup.getCupEmoji().getCode() != null ? cup.getCupEmoji().getCode().getValue() : null,
                cup.getCreatedAt()
        );
    }
}
