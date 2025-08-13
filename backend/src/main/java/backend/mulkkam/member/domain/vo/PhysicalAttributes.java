package backend.mulkkam.member.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_MEMBER_WEIGHT;

import backend.mulkkam.common.exception.CommonException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class PhysicalAttributes {

    private static final Double MIN_WEIGHT = 10.0;
    private static final Double MAX_WEIGHT = 250.0;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double weight;

    public PhysicalAttributes(Gender gender, Double weight) {
        validateWeightRange(weight);
        this.gender = gender;
        this.weight = weight;
    }

    private void validateWeightRange(Double weight) {
        if (weight == null) {
            return;
        }
        if (weight > MAX_WEIGHT || weight < MIN_WEIGHT) {
            throw new CommonException(INVALID_MEMBER_WEIGHT);
        }
    }

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
