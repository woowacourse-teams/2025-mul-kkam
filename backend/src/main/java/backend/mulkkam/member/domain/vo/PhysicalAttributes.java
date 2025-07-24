package backend.mulkkam.member.domain.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PhysicalAttributes {

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double weight;

    public Gender getGender() {
        if (gender == null) {
            return Gender.MALE;
        }
        return gender;
    }

    public Double getWeight() {
        if (weight == null) {
            return 60.0;
        }
        return weight;
    }
}
