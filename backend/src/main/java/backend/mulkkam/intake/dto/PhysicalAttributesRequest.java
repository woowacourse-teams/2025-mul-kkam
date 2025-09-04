package backend.mulkkam.intake.dto;

import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신체 정보 요청")
public record PhysicalAttributesRequest(
        @Schema(description = "성별", example = "MALE", implementation = Gender.class)
        Gender gender,

        @Schema(description = "체중 (kg)", example = "70.5")
        Double weight
) {

    public PhysicalAttributes toPhysicalAttributes() {
        return new PhysicalAttributes(gender, weight);
    }
}
