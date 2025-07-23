package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.vo.Gender;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class PhysicalAttributes {

    private Gender gender;
    private Double weight;

    protected PhysicalAttributes() {}

    public PhysicalAttributes(Gender gender, Double weight) {
        this.gender = gender;
        this.weight = weight;
    }
}
