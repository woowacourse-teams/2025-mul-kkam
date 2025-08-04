package backend.mulkkam.intake.dto;

import backend.mulkkam.member.domain.vo.Gender;

public record PhysicalAttributesRequest(
        Gender gender,
        Double weight
) {
}
