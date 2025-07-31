package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_SIZE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.RegisterCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            IntakeType intakeType = IntakeType.WATER;
            RegisterCupRequest registerCupRequest = new RegisterCupRequest(
                    cupNickname,
                    cupAmount,
                    intakeType
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            Cup savedCup = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(1))
                    .build();

            when(cupRepository.save(any(Cup.class))).thenReturn(savedCup);

            // when
            CupResponse cupResponse = cupService.create(
                    registerCupRequest,
                    member.getId()
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupResponse.cupNickname()).isEqualTo(cupNickname);
                softly.assertThat(cupResponse.cupAmount()).isEqualTo(cupAmount);
            });
        }

        @DisplayName("용량이 음수면 예외가 발생한다")
        @Test
        void error_amountLessThan0() {
            // given
            RegisterCupRequest registerCupRequest = new RegisterCupRequest(
                    "스타벅스",
                    -100,
                    IntakeType.WATER
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    member.getId()
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_AMOUNT.name());
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            RegisterCupRequest registerCupRequest = new RegisterCupRequest(
                    "스타벅스",
                    0,
                    IntakeType.WATER
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    member.getId()
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_AMOUNT.name());
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            RegisterCupRequest registerCupRequest = new RegisterCupRequest(
                    "스타벅스",
                    500,
                    IntakeType.WATER
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(1))
                    .build();
            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(2))
                    .build();
            Cup cup3 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(3))
                    .build();

            List<Cup> cups = List.of(
                    cup1,
                    cup2,
                    cup3
            );

            // when
            when(cupRepository.findAllByMemberIdOrderByCupRankAsc(member.getId())).thenReturn(cups);

            // then
            assertThatThrownBy(() -> cupService.create(
                    registerCupRequest,
                    member.getId()
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_SIZE.name());
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class Read {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member member = MemberFixtureBuilder.builder().build();

            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(2))
                    .cupAmount(new CupAmount(500))
                    .build();

            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(1))
                    .cupAmount(new CupAmount(1000))
                    .build();
            List<Cup> cups = List.of(cup2, cup1);

            when(cupRepository.findAllByMemberIdOrderByCupRankAsc(member.getId())).thenReturn(cups);

            // when
            CupsResponse cupsResponse = cupService.readCupsByMemberId(member.getId());

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

    @DisplayName("컵을 수정할 때에")
    @Nested
    class Modify {

        @DisplayName("컵 이름 및 용량 및 타입이 수정된다")
        @Test
        void success_withValidData() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;
            IntakeType beforeIntakeType = IntakeType.WATER;

            Cup cup = CupFixtureBuilder
                    .withMember(member)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .intakeType(beforeIntakeType)
                    .build();

            given(cupRepository.findById(cup.getId())).willReturn(Optional.of(cup));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType
            );

            // when
            cupService.update(
                    cup.getId(),
                    member.getId(),
                    updateCupRequest
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cup.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(cup.getNickname().value()).isEqualTo(afterCupNickName);
                softly.assertThat(cup.getIntakeType()).isEqualTo(afterIntakeType);
            });
        }

        @DisplayName("수정할 컵만 변경된다")
        @Test
        void success_whenCertainCupChanges() {
            // given
            Member member = MemberFixtureBuilder.builder().build();

            String beforeCupNickName1 = "변경 전1";
            Integer beforeCupAmount1 = 300;
            IntakeType beforeIntakeType1 = IntakeType.COFFEE;

            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1))
                    .intakeType(beforeIntakeType1)
                    .build();

            String beforeCupNickName2 = "변경 전2";
            Integer beforeCupAmount2 = 500;
            IntakeType beforeIntakeType2 = IntakeType.COFFEE;

            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .intakeType(beforeIntakeType2)
                    .build();

            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
            given(cupRepository.findById(cup1.getId())).willReturn(Optional.of(cup1));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType
            );

            // when
            cupService.update(
                    cup1.getId(),
                    member.getId(),
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
                    .build();
            Member member2 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("멤버2"))
                    .build();

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMember(member1)
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .build();

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    IntakeType.WATER
            );
            given(memberRepository.findById(member2.getId()))
                    .willReturn(Optional.of(member2));
            given(cupRepository.findById(cup.getId()))
                    .willReturn(Optional.of(cup));

            // when & then
            assertThatThrownBy(() -> cupService.update(
                    cup.getId(),
                    member2.getId(),
                    updateCupRequest
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_CUP.name());
        }
    }
}
