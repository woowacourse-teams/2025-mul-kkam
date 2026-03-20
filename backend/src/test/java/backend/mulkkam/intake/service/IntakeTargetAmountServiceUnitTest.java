package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.SuggestionIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.response.IntakeSuggestionAmountResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class IntakeTargetAmountServiceUnitTest {

    @InjectMocks
    IntakeAmountService intakeAmountService;

    @Mock
    IntakeHistoryRepository intakeHistoryRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @DisplayName("하루 섭취 목표 음용량을 수정할 때에")
    @Nested
    class ModifyTarget {

        private final Long memberId = 1L;
        private final Member member = MemberFixtureBuilder
                .builder()
                .buildWithId(memberId);

        @BeforeEach
        void setup() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        }

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            int newTargetAmount = 1_000;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when
            intakeAmountService.modifyTarget(new MemberDetails(member), intakeTargetAmountModifyRequest);

            // then
            assertThat(member.getTargetAmount()).isEqualTo(new TargetAmount(newTargetAmount));
        }

        @DisplayName("용량이 음용인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            int newTargetAmount = -1;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when & then
            assertThatThrownBy(
                    () -> intakeAmountService.modifyTarget(new MemberDetails(member), intakeTargetAmountModifyRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_TARGET_AMOUNT.name());
        }
    }

    @DisplayName("하루 섭취 목표 응용량을 추천받을 때에")
    @Nested
    class GetRecommended {

        private final Long memberId = 1L;
        private final Member member = MemberFixtureBuilder
                .builder()
                .gender(null)
                .weight(null)
                .buildWithId(memberId);

        @BeforeEach
        void setup() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        }

        @DisplayName("멤버의 신체 정보에 따라 추천 음용량이 계산된다")
        @Test
        void success_physicalAttributes() {
            // when
            IntakeSuggestionAmountResponse intakeSuggestionAmountResponse = intakeAmountService.getRecommended(
                    new MemberDetails(member)
            );

            // then
            assertThat(intakeSuggestionAmountResponse.amount()).isEqualTo(1_800);
        }

        @DisplayName("멤버 신체 정보가 없을 경우 기본 값들로 계산된다")
        @Test
        void success_physicalAttributesIsNotExisted() {
            // when
            IntakeSuggestionAmountResponse intakeSuggestionAmountResponse = intakeAmountService.getRecommended(
                    new MemberDetails(member)
            );

            // then
            assertThat(intakeSuggestionAmountResponse.amount()).isEqualTo(1_800);
        }
    }

    @DisplayName("사용자의 신체적 속성으로 추천 음용량을 조회하려고 할 때")
    @Nested
    class GetRecommendedTargetTargetAmount {

        @DisplayName("멤버의 신체 정보에 따라 추천 음용량이 계산된다")
        @Test
        void success_physicalAttributes() {
            // given
            PhysicalAttributesRequest physicalAttributesRequest = new PhysicalAttributesRequest(Gender.FEMALE, 60.0);

            // when
            SuggestionIntakeAmountResponse recommendedTargetAmount = intakeAmountService.getRecommendedTargetAmount(
                    physicalAttributesRequest);

            // then
            assertThat(recommendedTargetAmount.amount()).isEqualTo(1_800);
        }

        @DisplayName("멤버 신체 정보가 없을 경우 기본 값들로 계산된다")
        @Test
        void success_physicalAttributesIsNotExisted() {
            // given
            PhysicalAttributesRequest physicalAttributesRequest = new PhysicalAttributesRequest(null, null);

            // when
            SuggestionIntakeAmountResponse recommendedTargetAmount = intakeAmountService.getRecommendedTargetAmount(
                    physicalAttributesRequest);

            // then
            assertThat(recommendedTargetAmount.amount()).isEqualTo(1_800);
        }
    }
}
