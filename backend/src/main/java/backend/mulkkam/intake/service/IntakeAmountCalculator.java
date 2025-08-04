package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public interface IntakeAmountCalculator {

    Amount calculate(PhysicalAttributes physicalAttributes);
}
