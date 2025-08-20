package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CupServiceUnitTest {

    @Mock
    private CupRepository cupRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CupService cupService;

    @DisplayName("컵을 생성할 때")
    @Nested
    class Create {

        @DisplayName("정상적으로 생성한다")
        @Test
        void success_validData() {
            // given
            String cupNickname = "스타벅스";
            Integer cupAmount = 500;
            CreateCupRequest registerCupRequest = new CreateCupRequest(
                    cupNickname,
                    cupAmount,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);

            Cup savedCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(1))
                    .build();

            when(cupRepository.save(any(Cup.class))).thenReturn(savedCup);
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when
            CupResponse cupResponse = cupService.create(
                    registerCupRequest,
                    new MemberDetails(member)
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupResponse.cupNickname()).isEqualTo(cupNickname);
                softly.assertThat(cupResponse.cupAmount()).isEqualTo(cupAmount);
            });
        }

        @DisplayName("용량이 음용면 예외가 발생한다")
        @Test
        void error_amountLessThan0() {
            // given
            CreateCupRequest registerCupRequest = new CreateCupRequest(
                    "스타벅스",
                    -100,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    new MemberDetails(member)
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_AMOUNT.name());
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            CreateCupRequest registerCupRequest = new CreateCupRequest(
                    "스타벅스",
                    0,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    new MemberDetails(member)
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_AMOUNT.name());
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            CreateCupRequest registerCupRequest = new CreateCupRequest(
                    "스타벅스",
                    500,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);

            Cup cup1 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(1))
                    .build();
            Cup cup2 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(2))
                    .build();
            Cup cup3 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(3))
                    .build();

            List<Cup> cups = List.of(
                    cup1,
                    cup2,
                    cup3
            );

            // when
            when(cupRepository.countByMemberId(member.getId())).thenReturn(cups.size());
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    new MemberDetails(member)
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_COUNT.name());
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class ReadSortedCupsByMemberId {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);

            Cup cup1 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(2))
                    .cupAmount(new CupAmount(500))
                    .build();

            Cup cup2 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupRank(new CupRank(1))
                    .cupAmount(new CupAmount(1000))
                    .build();
            List<Cup> cups = List.of(cup2, cup1);

            when(cupRepository.findAllByMemberOrderByCupRankAsc(member)).thenReturn(cups);
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when
            CupsResponse cupsResponse = cupService.readSortedCupsByMember(new MemberDetails(member));

            CupResponse firstCup = cupsResponse.cups().getFirst();
            CupResponse secondCup = cupsResponse.cups().get(1);

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupsResponse.size()).isEqualTo(2);
                softly.assertThat(firstCup.cupNickname()).isEqualTo(cup2.getNickname().value());
                softly.assertThat(firstCup.cupAmount()).isEqualTo(cup2.getCupAmount().value());
                softly.assertThat(firstCup.cupRank()).isEqualTo(cup2.getCupRank().value());
                softly.assertThat(secondCup.cupRank()).isEqualTo(cup1.getCupRank().value());
                List<Integer> ranks = cupsResponse.cups().stream()
                        .map(CupResponse::cupRank)
                        .toList();
                softly.assertThat(ranks).isSorted();
            });
        }
    }

    @DisplayName("컵을 삭제할 때")
    @Nested
    class Delete {

        private final Member member = MemberFixtureBuilder.builder().build();
        private final Cup firstCup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .cupRank(new CupRank(1))
                .build();
        private final Cup secondCup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .cupRank(new CupRank(2))
                .build();
        private final Cup thirdCup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .cupRank(new CupRank(3))
                .build();

        @DisplayName("우선순위가 더 낮은 컵들의 우선순위가 한 단계씩 승격된다.")
        @Test
        void success_withLowerPriorityCups() {
            // given
            when(cupRepository.findByIdAndMember(firstCup.getId(), member)).thenReturn(Optional.of(firstCup));
            when(cupRepository.findAllByMember(member)).thenReturn(List.of(secondCup, thirdCup));
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when
            cupService.delete(firstCup.getId(), new MemberDetails(member));

            // then
            assertSoftly(softly -> {
                softly.assertThat(secondCup.getCupRank()).isEqualTo(new CupRank(1));
                softly.assertThat(thirdCup.getCupRank()).isEqualTo(new CupRank(2));
            });
        }

        @DisplayName("우선순위가 더 낮은 컵이 없는 경우, 그 어떤 컵의 우선순위도 승격되지 않는다.")
        @Test
        void success_withoutLowerPriorityCups() {
            // given
            when(cupRepository.findByIdAndMember(thirdCup.getId(), member)).thenReturn(Optional.of(thirdCup));
            when(cupRepository.findAllByMember(member)).thenReturn(List.of(firstCup, secondCup));
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            // when
            cupService.delete(thirdCup.getId(), new MemberDetails(member));

            // then
            assertSoftly(softly -> {
                softly.assertThat(firstCup.getCupRank()).isEqualTo(new CupRank(1));
                softly.assertThat(secondCup.getCupRank()).isEqualTo(new CupRank(2));
            });
        }
    }

    @DisplayName("컵을 수정할 때에")
    @Nested
    class Modify {

        @DisplayName("컵 이름 및 용량 및 타입이 수정된다")
        @Test
        void success_withValidData() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;
            IntakeType beforeIntakeType = IntakeType.WATER;

            Cup cup = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .intakeType(beforeIntakeType)
                    .build();

            given(cupRepository.findById(cup.getId())).willReturn(Optional.of(cup));
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;
            String afterEmoji = "emoji";

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType,
                    afterEmoji
            );

            // when
            cupService.update(
                    cup.getId(),
                    new MemberDetails(member),
                    updateCupRequest
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cup.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(cup.getNickname().value()).isEqualTo(afterCupNickName);
                softly.assertThat(cup.getIntakeType()).isEqualTo(afterIntakeType);
                softly.assertThat(cup.getCupEmoji()).isEqualTo(afterCupEmoji);
            });
        }

        @DisplayName("수정할 컵만 변경된다")
        @Test
        void success_whenCertainCupChanges() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            String beforeCupNickName1 = "변경 전1";
            Integer beforeCupAmount1 = 300;
            IntakeType beforeIntakeType1 = IntakeType.COFFEE;

            Cup cup1 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1))
                    .intakeType(beforeIntakeType1)
                    .buildWithId(1L);

            String beforeCupNickName2 = "변경 전2";
            Integer beforeCupAmount2 = 500;
            IntakeType beforeIntakeType2 = IntakeType.COFFEE;

            Cup cup2 = CupFixtureBuilder
                    .withMemberAndCupEmoji(member, cupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .intakeType(beforeIntakeType2)
                    .buildWithId(2L);

            given(cupRepository.findById(cup1.getId())).willReturn(Optional.of(cup1));
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;
            String afterEmoji = "emoji";

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType,
                    afterEmoji
            );

            // when
            cupService.update(
                    cup1.getId(),
                    new MemberDetails(member),
                    updateCupRequest
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cup1.getNickname().value()).isEqualTo(afterCupNickName);
                softly.assertThat(cup1.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(cup2.getNickname().value()).isEqualTo(beforeCupNickName2);
                softly.assertThat(cup2.getCupAmount().value()).isEqualTo(beforeCupAmount2);
            });
        }

        @DisplayName("멤버가 다를 경우 예외가 발생한다")
        @Test
        void error_ifTheMembersAreDifferent() {
            // given
            Member member1 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("멤버1"))
                    .buildWithId(1L);
            Member member2 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("멤버2"))
                    .buildWithId(2L);
            when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(member2));

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMemberAndCupEmoji(member1, cupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .buildWithId(1L);

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    IntakeType.WATER,
                    "emoji"
            );
            given(cupRepository.findById(cup.getId()))
                    .willReturn(Optional.of(cup));

            // when & then
            assertThatThrownBy(() -> cupService.update(
                    cup.getId(),
                    new MemberDetails(member2),
                    updateCupRequest
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_CUP.name());
        }
    }
}
