package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IntakeRecommendedAmountService {

    private final MemberRepository memberRepository;

    public ExtraIntakeAmount calculateExtraIntakeAmountBasedOnWeather(
            Long memberId,
            double averageTemperatureForDate
    ) {
        Member member = getMember(memberId);
        Double weight = member.getPhysicalAttributes().getWeight();

        return ExtraIntakeAmount.calculateWithAverageTemperature(averageTemperatureForDate, weight);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
