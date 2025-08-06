package backend.mulkkam.intake.dto;

import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record PhysicalAttributesRequest(
        Gender gender,
        Double weight
) {

    public PhysicalAttributes toPhysicalAttributes() {
        return new PhysicalAttributes(gender, weight);
    }
}
