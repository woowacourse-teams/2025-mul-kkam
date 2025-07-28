package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeAmountModifyRequest;
import backend.mulkkam.intake.dto.IntakeAmountResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class IntakeAmountServiceUnitTest {

    @InjectMocks
    IntakeAmountService intakeAmountService;

    @Mock
    MemberRepository memberRepository;

    @DisplayName("하루 섭취 목표 음용량을 수정할 때에")
    @Nested
    class ModifyTarget {

        public static final long MEMBER_ID = 1L;

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(MEMBER_ID))
                    .willReturn(Optional.of(mockMember));

            int newTargetAmount = 1_000;
            IntakeAmountModifyRequest intakeAmountModifyRequest = new IntakeAmountModifyRequest(newTargetAmount);

            // when
            intakeAmountService.modifyTarget(intakeAmountModifyRequest, MEMBER_ID);

            // then
            verify(memberRepository).findById(MEMBER_ID);
            verify(mockMember).updateTargetAmount(new Amount(newTargetAmount));
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            Member member = mock(Member.class);
            given(memberRepository.findById(MEMBER_ID))
                    .willReturn(Optional.ofNullable(member));

            int newTargetAmount = -1;
            IntakeAmountModifyRequest intakeAmountModifyRequest = new IntakeAmountModifyRequest(newTargetAmount);

            // when & then
            assertThatThrownBy(() -> intakeAmountService.modifyTarget(intakeAmountModifyRequest, MEMBER_ID))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(memberRepository).findById(MEMBER_ID);
            verify(member, never()).updateTargetAmount(any(Amount.class));
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            given(memberRepository.findById(MEMBER_ID))
                    .willReturn(Optional.empty());

            // when & then
            CommonException exception = assertThrows(CommonException.class,
                    () -> intakeAmountService.modifyTarget(any(IntakeAmountModifyRequest.class), MEMBER_ID));
            assertThat(exception.getErrorCode()).isEqualTo(NotFoundErrorCode.NOT_FOUND_MEMBER);
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
            Member member = MemberFixtureBuilder.builder()
                    .gender(null)
                    .weight(null)
                    .build();
            given(memberRepository.findById(MEMBER_ID))
                    .willReturn(Optional.of(member));

            // when
            IntakeAmountResponse intakeAmountResponse = intakeAmountService.getRecommended(MEMBER_ID);

            // then
            assertThat(intakeAmountResponse.amount()).isEqualTo(1800);
        }

        @DisplayName("멤버 신체 정보가 없을 경우 기본 값들로 계산된다")
        @Test
        void success_physicalAttributesIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .gender(null)
                    .weight(null)
                    .build();
            given(memberRepository.findById(MEMBER_ID))
                    .willReturn(Optional.of(member));

            // when
            IntakeAmountResponse intakeAmountResponse = intakeAmountService.getRecommended(MEMBER_ID);

            // then
            assertThat(intakeAmountResponse.amount()).isEqualTo(1800);
        }
    }
}
