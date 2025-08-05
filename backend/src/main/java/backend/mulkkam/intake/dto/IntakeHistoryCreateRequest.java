package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IntakeHistoryCreateRequest(
        LocalDateTime dateTime,
        int intakeAmount
) {
    public IntakeHistory toIntakeHistory(Member member) {
        return new IntakeHistory(
                member,
                dateTime,
                new Amount(intakeAmount),
                member.getTargetAmount()
        );
    }

    public LocalDate getDateTimeAsLocalDate() {
        return this.dateTime.toLocalDate();
    }
}
