package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.vo.Gender;
import lombok.Getter;

@Getter
public class PhysicalAttributes {

    private final Gender gender;
    private final Double weight;

    public PhysicalAttributes(PhysicalAttributesModifyRequest request) {
        this.gender = request.gender();
        this.weight = request.weight();
    }
}
