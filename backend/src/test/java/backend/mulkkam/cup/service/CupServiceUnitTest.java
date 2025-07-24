package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.BadRequestErrorCode.INVALID_CUP_SIZE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
import backend.mulkkam.support.CupFixture;
import backend.mulkkam.support.MemberFixture;
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
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    cupNickname,
                    cupAmount
            );
            Member member = new MemberFixture().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            Cup savedCup = new CupFixture()
                    .member(member)
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
                softly.assertThat(cupResponse.nickname()).isEqualTo(cupNickname);
                softly.assertThat(cupResponse.amount()).isEqualTo(cupAmount);
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
            Member member = new MemberFixture().build();
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
            Member member = new MemberFixture().build();
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
            Member member = new MemberFixture().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            Cup cup1 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(1))
                    .build();
            Cup cup2 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(2))
                    .build();
            Cup cup3 = new CupFixture()
                    .member(member)
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
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_SIZE);
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class Read {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member member = new MemberFixture().build();

            Cup cup1 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(2))
                    .cupAmount(new CupAmount(500))
                    .build();

            Cup cup2 = new CupFixture()
                    .member(member)
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
                softly.assertThat(firstCup.nickname()).isEqualTo(cup2.getNickname().value());
                softly.assertThat(firstCup.amount()).isEqualTo(cup2.getCupAmount().value());
                softly.assertThat(firstCup.rank()).isEqualTo(cup2.getCupRank().value());
                softly.assertThat(secondCup.rank()).isEqualTo(cup1.getCupRank().value());
                List<Integer> ranks = cupsResponse.cups().stream()
                        .map(CupResponse::rank)
                        .toList();
                softly.assertThat(ranks).isSorted();
            });
        }
    }
} 
