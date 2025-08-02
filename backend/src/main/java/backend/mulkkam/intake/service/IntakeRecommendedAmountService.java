package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IntakeRecommendedAmountService {

    private static final int EXTRA_INTAKE_TEMPERATURE_THRESHOLD = 26;
    private static final int WATER_INTAKE_COEFFICIENT_PER_DEGREE = 5;

    private final WeatherService weatherService;
    private final MemberRepository memberRepository;

    public double calculateAdditionalIntakeAmountByWeather(
            LocalDate targetDate,
            Long memberId
    ) {
        double averageTemperatureForDate = weatherService.getAverageTemperatureForDate(targetDate);

        Member member = getMember(memberId);
        Double weight = member.getPhysicalAttributes().getWeight();

        double additionalIntakeAmount = 0;
        if (averageTemperatureForDate > EXTRA_INTAKE_TEMPERATURE_THRESHOLD) {
            double differenceBetweenThreshold = averageTemperatureForDate - EXTRA_INTAKE_TEMPERATURE_THRESHOLD;
            additionalIntakeAmount = weight * WATER_INTAKE_COEFFICIENT_PER_DEGREE * differenceBetweenThreshold;
        }

        return additionalIntakeAmount;
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
