package backend.mulkkam.cup.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupNicknameAndAmountModifyRequest;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
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

import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.ErrorCode.NOT_PERMITTED_FOR_CUP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    cupNickname,
                    cupAmount,
                    "WATER",
                    "emoji"
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
                    cupRegisterRequest,
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
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    -100,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isSameAs(INVALID_CUP_AMOUNT);
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    0,
                    "WATER",
                    "emoji"
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isSameAs(INVALID_CUP_AMOUNT);
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    500,
                    "WATER",
                    "emoji"
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
            when(cupRepository.countByMemberId(member.getId())).thenReturn(cups.size());

            // then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isSameAs(INVALID_CUP_COUNT);
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

    @DisplayName("컵을 삭제할 때")
    @Nested
    class Delete {

        private final Member member = MemberFixtureBuilder.builder().build();
        private final Cup firstCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(1))
                .build();
        private final Cup secondCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(2))
                .build();
        private final Cup thirdCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(3))
                .build();

        @DisplayName("우선순위가 더 낮은 컵들의 우선순위가 한 단계씩 승격된다.")
        @Test
        void success_withLowerPriorityCups() {
            // given
            when(cupRepository.findByIdAndMemberId(firstCup.getId(), member.getId())).thenReturn(Optional.of(firstCup));
            when(cupRepository.findAllByMemberId(member.getId())).thenReturn(List.of(secondCup, thirdCup));

            // when
            cupService.delete(firstCup.getId(), member.getId());

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
            when(cupRepository.findByIdAndMemberId(thirdCup.getId(), member.getId())).thenReturn(Optional.of(thirdCup));
            when(cupRepository.findAllByMemberId(member.getId())).thenReturn(List.of(firstCup, secondCup));

            // when
            cupService.delete(thirdCup.getId(), member.getId());

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

        @DisplayName("컵 이름 및 용량이 수정된다")
        @Test
        void success_withValidData() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMember(member)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .build();

            given(cupRepository.findById(cup.getId())).willReturn(Optional.of(cup));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when
            cupService.modifyNicknameAndAmount(
                    cup.getId(),
                    member.getId(),
                    cupNicknameAndAmountModifyRequest
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cup.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(cup.getNickname().value()).isEqualTo(afterCupNickName);
            });
        }

        @DisplayName("수정할 컵만 변경된다")
        @Test
        void success_whenCertainCupChanges() {
            // given
            Member member = MemberFixtureBuilder.builder().build();

            String beforeCupNickName1 = "변경 전1";
            Integer beforeCupAmount1 = 300;
            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1))
                    .build();

            String beforeCupNickName2 = "변경 전2";
            Integer beforeCupAmount2 = 500;

            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .build();

            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
            given(cupRepository.findById(cup1.getId())).willReturn(Optional.of(cup1));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when
            cupService.modifyNicknameAndAmount(
                    cup1.getId(),
                    member.getId(),
                    cupNicknameAndAmountModifyRequest
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

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );
            given(memberRepository.findById(member2.getId()))
                    .willReturn(Optional.of(member2));
            given(cupRepository.findById(cup.getId()))
                    .willReturn(Optional.of(cup));

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.modifyNicknameAndAmount(
                            cup.getId(),
                            member2.getId(),
                            cupNicknameAndAmountModifyRequest)
            );
            assertThat(ex.getErrorCode()).isSameAs(NOT_PERMITTED_FOR_CUP);
        }
    }
}
