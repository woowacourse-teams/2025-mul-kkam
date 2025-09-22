package backend.mulkkam.intake.dto;

import backend.mulkkam.cup.domain.IntakeType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "음용량 기록 생성 응답")
public record CreateIntakeHistoryDetailResponse(
        @Parameter(description = "목표 달성률", example = "50.0")
        double achievementRate,
        @Parameter(description = "캐릭터 상태 설명 코멘트", example = "하뭉이는 신나요")
        String comment,
        @Parameter(description = "음용량", example = "300")
        int intakeAmount,
        @Parameter(description = "음용 타입", example = "COFFEE")
        IntakeType intakeType
) {
}
