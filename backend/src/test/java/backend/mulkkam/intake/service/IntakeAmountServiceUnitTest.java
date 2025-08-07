package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IntakeAmountServiceUnitTest {

    @InjectMocks
    IntakeAmountService intakeAmountService;

    @Mock
    IntakeHistoryRepository intakeHistoryRepository;

    @Mock
    MemberRepository memberRepository;

    @DisplayName("하루 섭취 목표 음용량을 수정할 때에")
    @Nested
    class ModifyTarget {

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            Member mockMember = mock(Member.class);

            int newTargetAmount = 1_000;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when
            intakeAmountService.modifyTarget(mockMember, intakeTargetAmountModifyRequest);

            // then
            verify(mockMember).updateTargetAmount(new Amount(newTargetAmount));
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            Member member = mock(Member.class);

            int newTargetAmount = -1;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when & then
            assertThatThrownBy(() -> intakeAmountService.modifyTarget(member, intakeTargetAmountModifyRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_AMOUNT.name());
            verify(member, never()).updateTargetAmount(any(Amount.class));
        }
    }

    @DisplayName("하루 섭취 목표 응용량을 추천받을 때에")
    @Nested
    class GetRecommended {

        public static final long MEMBER_ID = 1L;

        @DisplayName("멤버의 신체 정보에 따라 추천 음용량이 계산된다")
        @Test
        void success_physicalAttributes() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .gender(null)
                    .weight(null)
                    .buildWithId(1L);

            // when
            IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(
                    member);

            // then
            assertThat(intakeRecommendedAmountResponse.amount()).isEqualTo(1_800);
        }

        @DisplayName("멤버 신체 정보가 없을 경우 기본 값들로 계산된다")
        @Test
        void success_physicalAttributesIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .gender(null)
                    .weight(null)
                    .buildWithId(1L);

            // when
            IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(
                    member);

            // then
            assertThat(intakeRecommendedAmountResponse.amount()).isEqualTo(1_800);
        }
    }

    @DisplayName("사용자의 신체적 속성으로 추천 음용량을 조회하려고 할 때")
    @Nested
    class GetRecommendedTargetAmount {

        @DisplayName("멤버의 신체 정보에 따라 추천 음용량이 계산된다")
        @Test
        void success_physicalAttributes() {
            // given
            PhysicalAttributesRequest physicalAttributesRequest = new PhysicalAttributesRequest(Gender.FEMALE, 60.0);

            // when
            RecommendedIntakeAmountResponse recommendedTargetAmount = intakeAmountService.getRecommendedTargetAmount(
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
            RecommendedIntakeAmountResponse recommendedTargetAmount = intakeAmountService.getRecommendedTargetAmount(
                    physicalAttributesRequest);

            // then
            assertThat(recommendedTargetAmount.amount()).isEqualTo(1_800);
        }
    }
}
