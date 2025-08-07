package backend.mulkkam.intake.service;


import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.PhysicalAttributesRequest;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeRecommendedAmountResponse;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

class IntakeAmountServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private IntakeAmountService intakeAmountService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @DisplayName("하루 섭취 목표 음용량을 수정할 때에")
    @Nested
    class ModifyTarget {

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            int originTargetAmount = 2_000;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new Amount(originTargetAmount))
                    .build();
            Member savedMember = memberRepository.save(member);

            int newTargetAmount = 1_000;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when
            intakeAmountService.modifyTarget(savedMember, intakeTargetAmountModifyRequest);

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
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new Amount(originTargetAmount))
                    .build();
            Member savedMember = memberRepository.save(member);

            int newTargetAmount = -1;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when & then
            assertThatThrownBy(
                    () -> intakeAmountService.modifyTarget(savedMember, intakeTargetAmountModifyRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_AMOUNT.name());
        }

        @DisplayName("스냅샷이 저장된다")
        @Test
        void success_whenAmountIsModified() {
            // given
            int originTargetAmount = 2_000;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new Amount(originTargetAmount))
                    .build();
            memberRepository.save(member);

            int newTargetAmount = 1_000;
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    newTargetAmount);

            // when
            intakeAmountService.modifyTarget(member, intakeTargetAmountModifyRequest);
            Optional<TargetAmountSnapshot> targetAmountSnapshot = targetAmountSnapshotRepository.findByMemberIdAndUpdatedAt(
                    member.getId(), LocalDate.now());

            // then
            assertThat(targetAmountSnapshot.get().getTargetAmount().value()).isEqualTo(newTargetAmount);
        }

        @DisplayName("해당 수정이 추천에 의한 수정일 경우 금일 목표에만 반영된다")
        @Test
        void success_recommendAmount() {
            // given
            int memberTargetAmount = 1_500;
            Member member = MemberFixtureBuilder
                    .builder()
                    .targetAmount(new Amount(memberTargetAmount))
                    .build();

            memberRepository.save(member);
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.now())
                    .build();
            intakeHistoryRepository.save(intakeHistory);
            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest = new ModifyIntakeTargetAmountByRecommendRequest
                    (
                            1_000
                    );
            // when
            intakeAmountService.modifyDailyTargetBySuggested(member, modifyIntakeTargetAmountByRecommendRequest);
            Optional<IntakeHistory> findIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(
                    member,
                    LocalDate.now()
            );
            Optional<Member> findMember = memberRepository.findById(member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(findIntakeHistory).isPresent();
                softly.assertThat(findIntakeHistory.get().getTargetAmount().value()).isEqualTo(1000);
                softly.assertThat(findMember).isPresent();
                softly.assertThat(findMember.get().getTargetAmount().value()).isEqualTo(memberTargetAmount);
            });
        }

        @DisplayName("오늘의 기록이 있다면 금일 목표 음용량도 변경한다")
        @Test
        void success_changeTodayTargetAmount() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .targetAmount(new Amount(1500))
                    .build();
            memberRepository.save(member);
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.now())
                    .targetIntakeAmount(new Amount(1500))
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(1000);

            // when
            intakeAmountService.modifyTarget(member, intakeTargetAmountModifyRequest);
            Optional<IntakeHistory> findIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(
                    member, LocalDate.now());

            // then
            assertSoftly(softly -> {
                softly.assertThat(findIntakeHistory).isPresent();
                softly.assertThat(findIntakeHistory.get().getTargetAmount().value()).isEqualTo(1000);
            });
        }
    }

    @DisplayName("하루 섭취 목표 응용량을 추천받을 때에")
    @Nested
    class GetRecommended {

        @DisplayName("멤버의 신체 정보에 따라 추천 음용량이 계산된다")
        @Test
        void success_physicalAttributes() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .weight(60.0)
                    .build();
            Member savedMember = memberRepository.save(member);

            // when
            IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(
                    savedMember);

            // then
            assertThat(intakeRecommendedAmountResponse.amount()).isEqualTo(1_800);
        }

        @DisplayName("멤버 신체 정보가 없을 경우 기본 값들로 계산된다")
        @Test
        void success_physicalAttributesIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .weight(null)
                    .build();
            Member savedMember = memberRepository.save(member);

            // when
            IntakeRecommendedAmountResponse intakeRecommendedAmountResponse = intakeAmountService.getRecommended(
                    savedMember);

            // then
            assertThat(intakeRecommendedAmountResponse.amount()).isEqualTo(1_800);
        }
    }

    @DisplayName("하루 섭취 목표 음용량을 조회할 때에")
    @Nested
    class GetTarget {
        @Test
        @DisplayName("정상적으로 조회된다")
        void success_withExistedMember() {
            // given
            int expected = 1_000;
            Member member = MemberFixtureBuilder
                    .builder()
                    .targetAmount(new Amount(expected))
                    .build();
            Member savedMember = memberRepository.save(member);

            // when
            IntakeTargetAmountResponse actual = intakeAmountService.getTarget(savedMember);

            // then
            assertThat(actual.amount()).isEqualTo(expected);
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
