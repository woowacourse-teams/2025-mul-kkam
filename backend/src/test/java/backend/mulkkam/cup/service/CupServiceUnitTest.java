package backend.mulkkam.cup.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixture;
import backend.mulkkam.support.MemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
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
        void success() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    500);
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
                softly.assertThat(cupResponse.nickname()).isEqualTo("스타벅스");
                softly.assertThat(cupResponse.amount()).isEqualTo(500);
            });
        }

        @Disabled
        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest("스타벅스", 0);
            Member member = new MemberFixture().build();
            given(memberRepository.findById(member.getId()))
                    .willReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> cupService.create(cupRegisterRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
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
            assertThatThrownBy(() -> cupService.create(cupRegisterRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
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
            when(cupRepository.findAllByMemberId(member.getId())).thenReturn(cups);

            // then
            assertThatThrownBy(() -> cupService.create(cupRegisterRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
} 
