package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeAmountUpdateRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixture;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class IntakeAmountServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    IntakeAmountService intakeAmountService;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("하루 섭취 목표 음용량을 수정할 때에")
    @Nested
    class UpdateTarget {

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoeThan0() {
            // given
            int originTargetAmount = 2_000;
            Member member = new MemberFixture()
                    .targetAmount(new Amount(originTargetAmount))
                    .build();
            Member savedMember = memberRepository.save(member);

            int newTargetAmount = 1_000;
            IntakeAmountUpdateRequest intakeAmountUpdateRequest = new IntakeAmountUpdateRequest(newTargetAmount);

            // when
            intakeAmountService.updateTarget(intakeAmountUpdateRequest, savedMember.getId());

            // then
            Optional<Member> foundMember = memberRepository.findById(member.getId());
            assertSoftly(softly -> {
                softly.assertThat(foundMember).isPresent();
                softly.assertThat(foundMember.get().getTargetAmount()).isEqualTo(new Amount(newTargetAmount));
            });
        }

        @DisplayName("음용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            int originTargetAmount = 2_000;
            Member member = new MemberFixture()
                    .targetAmount(new Amount(originTargetAmount))
                    .build();
            Member savedMember = memberRepository.save(member);

            int newTargetAmount = -1;
            IntakeAmountUpdateRequest intakeAmountUpdateRequest = new IntakeAmountUpdateRequest(newTargetAmount);

            // when & then
            assertThatThrownBy(() -> intakeAmountService.updateTarget(intakeAmountUpdateRequest, savedMember.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            int newTargetAmount = 1_000;
            IntakeAmountUpdateRequest intakeAmountUpdateRequest = new IntakeAmountUpdateRequest(newTargetAmount);

            // when & then
            assertThatThrownBy(() -> intakeAmountService.updateTarget(intakeAmountUpdateRequest, Long.MAX_VALUE))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
