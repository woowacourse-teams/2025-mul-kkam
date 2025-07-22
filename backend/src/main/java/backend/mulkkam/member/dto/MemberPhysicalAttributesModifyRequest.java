package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.vo.Gender;

public record MemberPhysicalAttributesModifyRequest(
        Gender gender,
        Integer weight
) {
}
