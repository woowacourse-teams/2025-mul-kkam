package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.IntakeType;

public record UpdateCupRequest(
        String cupNickname,
        Integer cupAmount,
        IntakeType intakeType
) {
}
