package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.vo.Gender;

public record PhysicalAttributesModifyRequest(
        Gender gender,
        Integer weight
) {
}
