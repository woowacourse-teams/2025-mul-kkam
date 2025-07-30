package backend.mulkkam.cup.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
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

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
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
                    cupAmount
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
                    -100
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    0
            );
            Member member = MemberFixtureBuilder.builder().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    500
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
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_COUNT);
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
} 
