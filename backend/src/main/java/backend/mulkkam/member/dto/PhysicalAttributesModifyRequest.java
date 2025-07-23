package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record PhysicalAttributesModifyRequest(
        Gender gender,
        Double weight
) {

    public PhysicalAttributes toPhysicalAttributes() {
        return new PhysicalAttributes(
                gender,
                weight
        );
    }
}
